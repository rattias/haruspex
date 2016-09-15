package haruspex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ImmutableEntity extends ImmutableTraceElement implements IEntity {
  private final List<ImmutableBlock> blocks;
  
  ImmutableEntity(MutableEntity entity, ImmutableTrace trace) {
    super(trace, trace, entity.getID(), entity.getTags());   
    ArrayList<ImmutableBlock> tmp = new ArrayList<>();
    for (Iterator<MutableBlock> it = entity.blocks(); it.hasNext();) {
      tmp.add(new ImmutableBlock(it.next(), this));
    }
    blocks = Collections.unmodifiableList(tmp); 
  }

  @Override
  public Iterator<ImmutableBlock> blocks() {
    return blocks.iterator();
  }
  
  @Override
  public int blockCount() {
    return blocks.size();
  }
}
