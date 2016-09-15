package haruspex.model;

import java.util.Iterator;

public interface IEntity extends ITraceElement {
  Iterator<? extends IBlock> blocks();
  
  int blockCount();
}
