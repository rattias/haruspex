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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import haruspex.common.event.IEventSource;

public class DivinerMaster {
  private Worker[] workers;
  
  public DivinerMaster(int workerCount, String traceSourceClassName, String pythonPath) {
    workers = new Worker[workerCount];
    
    PathWatcher pw = new PathWatcher(pythonPath);    
    for (int i = 0; i < workerCount; i++) {
      workers[i] = new Worker(traceSourceClassName);
      pw.addListener(workers[i]);
    }
    // this causes all Workers to get notified about path, which in turn 
    // initializes the Interpreters for the first time
    pw.start();
    ExecutorService executor = Executors.newFixedThreadPool(workerCount);
    for (int i = 0; i < workerCount; i++) {
      executor.submit(workers[i]);
    }
  }
  
  private IEventSource createTraceSource(String className) {
    IEventSource traceSource = null;
    try {
      Class<?> clazz = Class.forName(className);
      traceSource = (IEventSource)clazz.newInstance();      
    } catch (ClassNotFoundException ex) {
      Diviner.fatalError("TraceSource class '" + className + "' not found in classpath");
    } catch (InstantiationException ex) {
      Diviner.fatalError("Unable to instantiate class '" + className + "':", ex);
    } catch (IllegalAccessException ex) {
      Diviner.fatalError("IllegalAccess to class '" + className + "':", ex);
    }     
    return traceSource;
  }
}
