package haruspex.batchdiviner;

import haruspex.model.ImmutableTrace;

public class Worker implements Runnable, PathChangeListener {
  private TraceEventSource traceSource;
  private final PythonProcessor processor;

  public Worker(String traceSourceClassName) {
    try {
      traceSource = (TraceEventSource)Class.forName(traceSourceClassName).newInstance();
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
