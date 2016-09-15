package haruspex.model;

import java.util.ArrayList;
import java.util.Iterator;

import haruspex.common.CoreTags;
import haruspex.common.ID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public class MutableBlock extends MutableTraceElement implements AutoCloseable, IBlock {
  private int pointIdCounter;  
  private final ArrayList<MutablePoint> points = new ArrayList<MutablePoint>();
  private boolean closed = false;
  
  MutableBlock(
      ID id, 
      MutableTrace trace, 
      MutableEntity entity, 
      String blockName, 
      Tag...blockTags) {
    this(id, trace, entity, blockName, blockTags, TagList.EMPTY_ARRAY);
  }

  MutableBlock(
      ID id, 
      MutableTrace trace, 
      MutableEntity entity, 
      String blockName, 
      Tag[] blockTags, 
      Tag...pointTags) {
    super(trace, entity, id, blockTags);
    addAll(CoreTags.name(blockName));
    point("", 0, pointTags);
  }

  
  public MutablePoint point(String name, long timestamp, Tag...tags) {
    if (closed) {
      throw new IllegalStateException("block was previously closed");
    }
    MutablePoint p = new MutablePoint(getTrace(), this, new LocalID(pointIdCounter++), timestamp, tags);
    points.add(p);
    return p;
  }

  @Override
  public Iterator<MutablePoint> points() {
    return points.iterator();
  }
  
  @Override
  public int pointCount() {
    return points.size();
  }
  
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);  
  }

  public void close(Tag[] blockTags, Tag...pointTags) {
    addAll(blockTags);
    point("", 0, pointTags);
    closed = true;
  } 
}

