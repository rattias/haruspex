package haruspex.producer;

import haruspex.common.CoreTags;
import haruspex.common.ID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

/**
 * A {@code Block} represents an interval of time. {@code Block}s are created from {@link Entity} by calling
 * the {@link Entity#block(String, Tag...)} or {@link Entity#block(String, Tag[], Tag...)} methods. 
 * {@code Block}s are containers for <i>points</i>, which represent moments in time with, possibly, associated 
 * tags. 
 * 
 * Creation of a {@code Block} results in a point, called <i>begin point</i>, instantiated implicitly. 
 * If a block is closed (which should normally be the case) then <i>end point</i> is implicitly created. 
 * 
 * Additional points can be created explicitly calling the {@link #point} method. This method does not
 * return any model element for the point, as there are no additional actions that can be taken on it after
 * creation.
 * 
 * Among the various tags that can be associated to a point, particularly interesting are the 
 * <i>cause</i> and <i>effect</i>. Tagging a pair of points as (cause, effect) indicates a causality 
 * relationship between the two points. 
 *
 */
public class Block extends TraceElement implements AutoCloseable {
  private boolean isClosed;
  
  public final static Block GHOST =  new Block(LocalID.GHOST, Trace.GHOST, Entity.GHOST, TraceSerializer.GHOST, null) {
    @Override
    public void point(String name, Tag...pointTags) {  
    }
  
    @Override
    public void close(Tag[] blockTags, Tag...pointTags) {    
    }
  };
  
  Block(ID id, Trace trace, Entity entity, TraceSerializer serializer, String blockName, Tag...blockTags) {
    super(trace, entity, id, serializer);
    serializer.serialize(
        trace.seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{trace.getID(), entity.getID(), id},       
        TagList.of(TagList.Context.BLOCK, blockTags).addAll(Tag.of(CoreTags.NAME, blockName))        
    );
  }

  Block(ID id, Trace trace, Entity entity, TraceSerializer serializer, String blockName, Tag[] blockTags, Tag...pointTags) {
    super(trace, entity, id, serializer);
    serializer.serialize(
        trace.seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{trace.getID(), entity.getID(), id},
        TagList.of(TagList.Context.BLOCK, blockTags).addAll(Tag.of(CoreTags.NAME, blockName)),
        TagList.of(TagList.Context.POINT, pointTags)
    );
  }

  /**
   * emits a point for this block with the specified name and tags 
   * @param name name for the point
   * @param pointTags tags
   */
  public void point(String name, Tag...pointTags) {
    if (isClosed) {
      throw new IllegalStateException("Block had been previously closed.");
    }
    getSerializer().serialize(
        getTrace().seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{getTrace().getID(), getParent().getID(), getID()},
        TagList.of(TagList.Context.POINT,
            Tag.of(CoreTags.NAME, name)
        ).addAll(pointTags)
    );
  }
  
  /**
   * closes the block, creating implicit an end point for it.
   */
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);  
  }

  /**
   * closes the block, creating an end point for it with the {@code pointTags} tags.
   * @param blockTags tags for the block
   * @param pointTags tags for the end point
   */
  public void close(Tag[] blockTags, Tag...pointTags) {
    getSerializer().serialize(
        getTrace().seqNum(),
        getTrace().getClockDomain().getTime(),
        new ID[]{getTrace().getID(), getParent().getID(), getID()},
        TagList.of(TagList.Context.BLOCK,CoreTags.endBlock()).addAll(blockTags),
        TagList.of(TagList.Context.POINT, pointTags)  
    );
    isClosed = true;
  }
  
}

