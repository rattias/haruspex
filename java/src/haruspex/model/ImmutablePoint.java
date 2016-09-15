package haruspex.model;

public class ImmutablePoint extends ImmutableTraceElement implements IPoint {
  private final long timestamp;
  
  ImmutablePoint(MutablePoint point, ImmutableBlock block) {
    super(block.getTrace(), block, point.getID(), point.getTags());
    this.timestamp = point.timestamp();
  }

  public long timestamp() {
    return timestamp;
  }
  
}
