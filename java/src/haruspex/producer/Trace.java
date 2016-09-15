package haruspex.producer;

import java.util.concurrent.atomic.AtomicLong;

import haruspex.common.ClockDomain;
import haruspex.common.CoreTags;
import haruspex.common.DefaultClockDomain;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;
/**
 * A {@code Trace} is a {@link TraceElement} represents a portion of a trace created by a particular process.
 * As any {@code TraceElement}, it is characterized by an {@link haruspex.common.ID}, and a, possibly empty,
 * set of {@link haruspex.common.Tag Tags}.
 *   
 * A trace object is created through a Builder. 
 * Typically one process creates the first Trace instance for a particular trace using the 
 * {@link Builder#build(Tag...) build(Tag...)} method , which creates an instance with a random ID. The process then
 * populates the trace with {@link Entity entities} representing the various concurrent elements within the process. 
 *  
 *
 * When interacting with a different process, for example through an RPC call,
 * the process sends the ID that is associated to the trace over. The receiver process will in turn instantiate
 * a trace using the {@link Builder#build(haruspex.common.GlobalID id, haruspex.common.Tag...) 
 * build(ID id, Tag...tags)} method of the Builder, passing the ID received. 
 * 
 * A {@code Trace} can contain zero or more {@link Entity}. The unit of concurrency represented could be 
 * a Thread or the entire process, but it could also be some user-defined unit such as a context which flows 
 * through various threads.
 * 
 */
public class Trace extends TraceElement implements AutoCloseable {  
  public final static Trace GHOST = new Trace(TraceSerializer.GHOST, GlobalID.GHOST, DefaultClockDomain.INSTANCE);
    
  private final AtomicLong seqNum;
  private final ClockDomain clockDomain;
      
  private Trace(TraceSerializer serializer, GlobalID id, ClockDomain clockDomain, Tag...tags) {
    super(null, null, id, serializer);
    this.clockDomain = clockDomain;
    this.seqNum = new AtomicLong();
    serializer.serialize(
        seqNum(),
        clockDomain.getTime(),
        new ID[]{getID()},
        TagList.of(TagList.Context.TRACE,
            Tag.of(CoreTags.TIME_UNIT, clockDomain.getTimeUnit()),
            Tag.of(CoreTags.CLOCK_DOMAIN, clockDomain.getName()),
            Tag.of(CoreTags.CLOCK_MAX_SKEW, clockDomain.getMaxSkew())
        ).addAll(tags)
    );
  }

  /**
   * @return a progressive sequence number
   */
  long seqNum() {
    return seqNum.getAndIncrement();
  }
  
//  /**
//   * creates an entity with the specified name and a globally-unique ID.
//   * @param name name given to the entity
//   * @return the {@link Entity entity} object
//   */
//  public Entity entity(String name) {
//    return entity(name, TagList.EMPTY_ARRAY);
//  }

  /**
   * 
   * @param name the name given to the entity
   * @param tags a list of tags to be associated to the entity
   * @return the {@link Entity entity}
   */
  public Entity entity(String name, Tag...tags) {
    return new Entity(this, getSerializer(), name, tags);
  }
  
  /**
   * 
   * @return the {@link ClockDomain ClockDomain} associated to the trace
   */
  public ClockDomain getClockDomain() {
    return clockDomain;
  }
  
  /**
   * closes the trace.
   */
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);
  }
  
  /** 
   * closes the trace, associating the specified tags to it
   * @param tags list of tags
   */
  public void close(Tag...tags) {
    getSerializer().serialize(
        getTrace().seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{getTrace().getID(), getID()},
        TagList.of(TagList.Context.TRACE,
            CoreTags.endEntity()
        ).prependAll(tags)
    );    
  }

  
  /**
   * A builder for Trace objects 
   */
  public static class Builder {
    private ClockDomain clockDomain = DefaultClockDomain.INSTANCE;
    private SamplingPolicy samplingPolicy = SamplingPolicies.ALWAYS;
    private TraceSerializer serializer = TraceSerializer.GHOST;

    /**
     * 
     * @param serializer the {@link TraceSerializer serializer} to be used for the trace
     * @return the builder
     */
    public Builder setSerializer(TraceSerializer serializer) {
      this.serializer = serializer;
      return this;
    }

    /**
     * 
     * @param clockDomain the {@link ClockDomain ClockDomain} to be used for the trace
     * @return the builder
     */
    public Builder setClockDomain(ClockDomain clockDomain) {
      this.clockDomain = clockDomain;
      return this;
    }
    
    /**
     * 
     * @param samplingPolicy {@link SamplingPolicy SamplingPolicy} to be used for the trace
     * @return the builder
     */
    public Builder setSamplingPolicy(SamplingPolicy samplingPolicy) {
      this.samplingPolicy = samplingPolicy;
      return this;
    }

    /**
     * 
     * @param tags a list of tags associated to the trace
     * @return the Trace object
     */
    public Trace build(Tag...tags) {
      return build(GlobalID.random(), tags);
    }
    
    /**
     * 
     * @param id the id from a Trace object created in a different process
     * @param tags a list of tags
     * @return the Trace object
     */
    public Trace build(GlobalID id, Tag...tags) {
      return samplingPolicy.shouldTrace()
        ? new Trace(serializer, id, clockDomain, tags)
        : Trace.GHOST;
    }
  }
}

