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

import haruspex.common.EventType;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;
import haruspex.common.event.IEventSink;

/**
 * An {@code Entity} captures a unit of distributed computation such as a thread. Entities are created
 * from {@link Trace} objects by calling the {@link Trace#entity(String, Tag...)} method.
 * 
 */
public class Entity extends TraceElement implements AutoCloseable {
  public final static Entity GHOST = new Entity(Trace.GHOST, IEventSink.GHOST, "<ghost>");
  private boolean isClosed;
  private int blockIdCounter;
  
  Entity(Trace trace, IEventSink serializer, String name, Tag...tags) {
    super(trace, trace, GlobalID.random(), serializer);
    serializer.put(
        trace.seqNum(),
        trace.getClockDomain().getTime(),
        new ID[]{trace.getID(), getID()},
        TagList.of(tags, EventType.BEGIN_ENTITY.getTag(), Tag.name(name)
        )
    );
  }
  
  /**
   * starts a {@link Block} with the specified tags. A block always has a starting point created implicitly. 
   * @param name the block name
   * @param blockTags tags to be emitted associated to the block
   * @return a {@code Block} instance
   */
  public Block block(Tag...blockTags) {
    if (isClosed) {
      throw new IllegalStateException("Entity was closed");
    }
    return new Block(
        LocalID.of(blockIdCounter++), 
        getTrace(), 
        this, 
        getSink(), 
        blockTags);
  }

  /**
   * starts a {@link Block} tagged with the {@code blockTags} tags. A block always has a starting point created
   *  implicitly and tagged with the {@code pointTags} tags. 
   * @param name the block name
   * @param blockTags tags to be emitted associated to the block
   * @param pointTags tags to be emitted associated to the point
   * @return a {@code Block} instance
   */
  public Block block(Tag[] blockTags, Tag...pointTags) {
    if (isClosed) {
      throw new IllegalStateException("Entity was closed");
    }
    return new Block(
        LocalID.of(blockIdCounter++), 
        getTrace(), 
        this, getSink(), 
        blockTags, 
        pointTags);
  }

  /**
   * Closes the entity. A closed entity cannot have new blocks created on it.
   */
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);  
  }

  
  public void close(Tag...tags) {
    getSink().put(
        getTrace().seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{getTrace().getID(), getID()},
        TagList.of(tags, EventType.END_ENTITY.getTag())
    );
    isClosed = true;
  }
  
}
