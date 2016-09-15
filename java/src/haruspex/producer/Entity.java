package haruspex.producer;

import haruspex.common.CoreTags;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

/**
 * An {@code Entity} captures a unit of distributed computation such as a thread. Entities are created
 * from {@link Trace} objects by calling the {@link Trace#entity(String, Tag...)} method.
 * 
 */
public class Entity extends TraceElement implements AutoCloseable {
  public final static Entity GHOST = new Entity(Trace.GHOST, TraceSerializer.GHOST, "<ghost>");
  private boolean isClosed;
  private int blockIdCounter;
  
  Entity(Trace trace, TraceSerializer serializer, String name, Tag...tags) {
    super(trace, trace, GlobalID.random(), serializer);
    serializer.serialize(
        trace.seqNum(),
        trace.getClockDomain().getTime(),
        new ID[]{trace.getID(), getID()},
        TagList.of(TagList.Context.ENTITY, Tag.of(CoreTags.NAME, name)).addAll(tags)
    );
  }
  
  /**
   * starts a {@link Block} with the specified tags. A block always has a starting point created implicitly. 
   * @param name the block name
   * @param blockTags tags to be emitted associated to the block
   * @return a {@code Block} instance
   */
  public Block block(String name, Tag...blockTags) {
    if (isClosed) {
      throw new IllegalStateException("Entity was closed");
    }
    return new Block(new LocalID(blockIdCounter++), getTrace(), this, getSerializer(), name, blockTags);
  }

  /**
   * starts a {@link Block} tagged with the {@code blockTags} tags. A block always has a starting point created
   *  implicitly and tagged with the {@code pointTags} tags. 
   * @param name the block name
   * @param blockTags tags to be emitted associated to the block
   * @param pointTags tags to be emitted associated to the point
   * @return a {@code Block} instance
   */
  public Block block(String name, Tag[] blockTags, Tag...pointTags) {
    if (isClosed) {
      throw new IllegalStateException("Entity was closed");
    }
    return new Block(new LocalID(blockIdCounter++), getTrace(), this, getSerializer(), name, blockTags, pointTags);
  }

  /**
   * Closes the entity. A closed entity cannot have new blocks created on it.
   */
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);  
  }

  
  public void close(Tag...tags) {
    getSerializer().serialize(
        getTrace().seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{getTrace().getID(), getID()},
        TagList.of(TagList.Context.ENTITY,
            CoreTags.endEntity()
        ).prependAll(tags)
    );    
    isClosed = true;
  }
  
}
