package haruspex.batchdiviner;

import java.util.Iterator;
import java.util.List;

import haruspex.common.ID;

public class TraceEvents<T> implements Iterable<T> {
  private ID traceId;
  private int shard;
  private List<T> events;
  
  public TraceEvents(int shard, List<T> events) {
    this.shard = shard;
    this.events = events;
  }
  
  public int getShard() {
    return shard;
  }

  @Override
  public Iterator<T> iterator() {
    return events.iterator();
  }
  
  public int getEventCount() {
    return events.size();
  }
   
}
