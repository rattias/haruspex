package haruspex.model;

import java.util.Iterator;

public interface IBlock extends ITraceElement {
  Iterator<? extends IPoint> points();
  
  int pointCount();  
}
