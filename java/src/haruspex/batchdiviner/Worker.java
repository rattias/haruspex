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

import haruspex.common.event.IEventSource;
import haruspex.model.ImmutableTrace;

public class Worker implements Runnable, PathChangeListener {
  private IEventSource traceSource;
  private final PythonProcessor processor;

  public Worker(String traceSourceClassName) {
    try {
      traceSource = (IEventSource)Class.forName(traceSourceClassName).newInstance();
      traceSource.configure(Diviner.PROPERTIES);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
      Diviner.fatalError("Fatal error when instantiating trace source:", ex);
    }
    processor = new PythonProcessor();
  }
  
  @Override
  public void run() {
    while (true) {
      ImmutableTrace trace = null; // = traceSource.get();
      processor.process(trace);
    }
  }

  @Override
  public void pathChanged(String[] path) {
    processor.pathChanged(path);   
  }

}
