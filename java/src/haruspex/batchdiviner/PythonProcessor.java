/*
 * Copyright 2016 Roberto Attias
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package haruspex.batchdiviner;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import haruspex.common.Tag;
import haruspex.model.ImmutableTrace;

public class PythonProcessor implements PathChangeListener {
  interface Filter {
    boolean filter(Tag[] traceTags);
  }
  
  interface Processor {
    boolean process(ImmutableTrace trace);
  }

  class Module {
    public final String name;
    public final Filter filter;
    public final Processor processor;
    
    public Module(String name, Filter filter, Processor processor) {
      this.name = name;
      this.filter = filter;
      this.processor = processor;
    }
  }

  
  private volatile PythonInterpreter interpreter;
  private volatile List<Module> modules;
  
  public PythonProcessor() {
  }

  private void updateInterpreter(String[] path) {

    PySystemState engineSys = new PySystemState();
    for (String p : path) {
      engineSys.path.append(Py.newString(p));
    }
    PythonInterpreter interpreter = new PythonInterpreter(new PyDictionary(), engineSys);
    List<Module> modules = new ArrayList<>();
    for (String p : path) {
      traverse(p, "", 0, modules);
    }
    synchronized(this) {
      this.interpreter = interpreter;
      this.modules = modules;
    }
  }

  private static boolean isModule(File f, String fullpath) {
    return f.isFile() && fullpath.endsWith(".py") && !fullpath.endsWith("__init__.py");
  }

  private static String pathToName(String path) {
    final String res = path.replace('/', '.');
    return res.endsWith(".py") ? res.substring(0, path.length() - 3) : res;

  }

  private void traverse(String pathEl, String fpath, int level, List<Module> modules) {
    final String fullpath = fpath.equals("") ? pathEl : pathEl + "/" + fpath;
    final File f = new File(fullpath);
    if (f.isDirectory()) {
      final String fn = fullpath + "/__init__.py";
      if (new File(fn).exists()) {
        if (!fpath.equals("")) {
          final String pkg = pathToName(fpath);
          interpreter.exec("import " + pkg);
        }
        final String[] subFiles = f.list();
        for (final String s : subFiles)
          traverse(pathEl, fpath.equals("") ? s : fpath + "/" + s, level + 1, modules);
      }
    } else {
      if (isModule(f, fullpath)) {
        final String name = pathToName(fpath);
        interpreter.exec("import " + name);
        PyObject filter = interpreter.get(name + ".haruspex_filter");
        if (filter == null || !(filter instanceof PyFunction)) {
          return;
        }
        PyObject processor = interpreter.get(name + ".haruspex_processor");
        if (processor == null) {
          return;
        }
        modules.add(
            new Module(
                name, 
                (Filter)filter.__tojava__(Filter.class),
                (Processor)processor.__tojava__(Processor.class)
            )
        );        
      }
    }
  }


  public void process(ImmutableTrace trace) {
    List<Tag> tags = new ArrayList<>();
    for (Iterator<Tag> it = trace.getTagIterator(); it.hasNext();) {
      tags.add(it.next());
    }
    List<Module> localModules;
    synchronized(this) {
      localModules = this.modules;
    }
    
    for (Module module : localModules) {
      if (module.filter.filter(tags.toArray(new Tag[tags.size()]))) {
        module.processor.process(trace);
      }
    }
  }
   
  @Override
  public void pathChanged(String[] path) {
    updateInterpreter(path);
  }
}
  