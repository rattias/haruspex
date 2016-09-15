package haruspex.batchdiviner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
  
  private TraceEventSource createTraceSource(String className) {
    TraceEventSource traceSource = null;
    try {
      Class<?> clazz = Class.forName(className);
      traceSource = (TraceEventSource)clazz.newInstance();      
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
