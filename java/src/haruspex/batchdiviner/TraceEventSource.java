package haruspex.batchdiviner;

import java.util.List;
import java.util.Properties;

import haruspex.model.ImmutableTrace;

public interface TraceEventSource {
  void configure(Properties props);
  TraceEvents<TraceEvent>[] poll(long timeoutMs);
  TraceEvents<String>[] pollRaw(long timeoutMs);
  public boolean supportCheckpointing();
  public void checkpoint();
}
