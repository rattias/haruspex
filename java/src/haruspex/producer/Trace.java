/*
 * Copyright 2016 Roberto Attias
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package haruspex.producer;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

import haruspex.common.ClockDomain;
import haruspex.common.DefaultClockDomain;
import haruspex.common.EventType;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;
import haruspex.common.event.IEventSink;
import haruspex.common.event.PrintWriterSink;
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
  public final static Trace GHOST = new Trace(IEventSink.GHOST, GlobalID.GHOST, DefaultClockDomain.INSTANCE);
  private final AtomicLong seqNum;
  private final ClockDomain clockDomain;
      
  private Trace(IEventSink sink, GlobalID id, ClockDomain clockDomain, Tag...tags) {
    super(null, null, id, sink);
    this.clockDomain = clockDomain;
    this.seqNum = new AtomicLong();
    sink.put(
        seqNum(),
        clockDomain.getTime(),
        new ID[]{getID()},
        TagList.of(
            tags,
            EventType.BEGIN_TRACE.getTag(),
            Tag.clockTimeUnit(clockDomain.getTimeUnit()),
            Tag.clockDomain(clockDomain.getName()),
            Tag.clockMaxSkew(clockDomain.getMaxSkew())
        )
    );
  }

  /**
   * @return a progressive sequence number
   */
  long seqNum() {
    return seqNum.getAndIncrement();
  }
  
  /**
   * 
   * @param name the name given to the entity
   * @param tags a list of tags to be associated to the entity
   * @return the {@link Entity entity}
   */
  public Entity entity(String name, Tag...tags) {
    return new Entity(this, getSink(), name, tags);
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
    getSink().put(
        getTrace().seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{getTrace().getID(), getID()},
        TagList.of(tags, EventType.END_TRACE.getTag())
    );    
  }

  
  /**
   * A builder for Trace objects 
   */
  public static class Builder {
    private ClockDomain clockDomain = DefaultClockDomain.INSTANCE;
    private SamplingPolicy samplingPolicy = SamplingPolicies.ALWAYS;
    private IEventSink sink = null;

    /**
     * 
     * @param serializer the {@link IEventSink serializer} to be used for the trace
     * @return the builder
     */
    public Builder setSink(IEventSink sink) {
      this.sink = sink;
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
        ? new Trace(
            sink == null ? new PrintWriterSink(new PrintWriter(System.err)) : sink,
            id, 
            clockDomain, 
            tags)
        : Trace.GHOST;
    }
  }
}

