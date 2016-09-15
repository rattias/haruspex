package haruspex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import haruspex.common.ClockDomain;


public class ImmutableTrace extends ImmutableTraceElement implements ITrace {
  private final ClockDomain clockDomain;
  private final List<ImmutableEntity> entities;
  
  ImmutableTrace(MutableTrace trace) {
    super(null, null, trace.getID(), trace.getTags());
    this.clockDomain = trace.getClockDomain();
    ArrayList<ImmutableEntity> tmp = new ArrayList<>();
    for (Iterator<MutableEntity> it = trace.entities(); it.hasNext();) {
      tmp.add(new ImmutableEntity(it.next(), this));
    }
    entities = Collections.unmodifiableList(tmp);
  }

  public ClockDomain getClockDomain() {
    return clockDomain;
  }
  
  public Iterator<ImmutableEntity> entities() {
    return entities.iterator();
  }
  
  @Override
  public int entityCount() {
    return entities.size();
  }
}

