package haruspex.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import haruspex.common.ClockDomain;
import haruspex.common.DefaultClockDomain;
import haruspex.common.GlobalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public class MutableTrace extends MutableTraceElement implements ITrace {
  private final AtomicLong seqNum;
  private final ClockDomain clockDomain;
  private ArrayList<MutableEntity> entities = new ArrayList<>();
  
  public MutableTrace() {
    this(GlobalID.random());
  }

  public MutableTrace(GlobalID id) {
    this(id, TagList.EMPTY_ARRAY);
  }

  public MutableTrace(Tag...tags) {
    this(GlobalID.random(), tags);
  }
  
  private MutableTrace(GlobalID id, Tag...tags) {
    this(id, DefaultClockDomain.INSTANCE, tags);
  }
  
  private MutableTrace(GlobalID id, ClockDomain clockDomain, Tag...tags) {
    super(null, null, id);
    this.clockDomain = clockDomain;
    this.seqNum = new AtomicLong();
  }

  long seqNum() {
    return seqNum.getAndIncrement();
  }
  
  public MutableEntity entity(String name) {
    return entity(name, TagList.EMPTY_ARRAY);
  }

  public MutableEntity entity(String name, Tag...tags) {
    MutableEntity entity = new MutableEntity(this, name, tags);
    entities.add(entity);
    return entity;
  }
  
  @Override
  public ClockDomain getClockDomain() {
    return clockDomain;
  }
  
  @Override
  public Iterator<MutableEntity> entities() {
    return entities.iterator();
  }

  public int entityCount() {
    return entities.size();
  }

  public void remove(MutableEntity entity) {
    entities.remove(entity);
  }
  
  public ImmutableTrace toImmutable() {
    return new ImmutableTrace(this);
  }
}

