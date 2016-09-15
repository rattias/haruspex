package haruspex.model;

import haruspex.common.ID;
import haruspex.common.Tag;

public class MutablePoint extends MutableTraceElement implements IPoint {
  private long timestamp;
  
  MutablePoint(MutableTrace trace, MutableTraceElement parent, ID id, long timestamp, Tag...tags) {
    super(trace, parent, id, tags);
    this.timestamp = timestamp;
  }
  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  @Override
  public long timestamp() {
    return timestamp;
  }
}
