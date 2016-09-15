package haruspex.model;

import java.util.Iterator;

import haruspex.common.ClockDomain;

public interface ITrace extends ITraceElement {
  ClockDomain getClockDomain();
  Iterator<? extends IEntity> entities();
  int entityCount();
}
