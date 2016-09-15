package haruspex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class ImmutableBlock extends ImmutableTraceElement implements IBlock {
  private final List<ImmutablePoint> points;
  
  ImmutableBlock(MutableBlock block, ImmutableEntity entity) {
    super(entity.getTrace(), entity, block.getID(), block.getTags());
    ArrayList<ImmutablePoint> tmp = new ArrayList<>();
    for (Iterator<MutablePoint> it = block.points(); it.hasNext();) {
      tmp.add(new ImmutablePoint(it.next(), this));
    }
    points = Collections.unmodifiableList(tmp); 
  }


  public Iterator<ImmutablePoint> points() {
    return points.iterator();
  }
  
  @Override
  public int pointCount() {
    return points.size();
  }
}

