package haruspex.model;

import java.util.ArrayList;
import java.util.Iterator;

import haruspex.common.CoreTags;
import haruspex.common.GlobalID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public class MutableEntity extends MutableTraceElement implements AutoCloseable, IEntity {
  private int blockIdCounter;
  private ArrayList<MutableBlock> blocks = new ArrayList<>();
  
  MutableEntity(MutableTrace trace, String name, Tag...tags) {
    super(trace, trace, GlobalID.random(), tags);
    addAll(CoreTags.name(name));
  }

  public MutableBlock block(String name) {
    return block(name, TagList.EMPTY_ARRAY);
  }


  public MutableBlock block(String name, Tag...blockTags) {
    return block(name, blockTags, TagList.EMPTY_ARRAY);
  }

  public MutableBlock block(String name, Tag[] blockTags, Tag...pointTags) {
    MutableBlock block = new MutableBlock(new LocalID(blockIdCounter++), getTrace(), this, name, blockTags, pointTags);
    blocks.add(block);
    return block;
  }
  
  @Override
  public Iterator<MutableBlock> blocks() {
    return blocks.iterator();
  }

  @Override
  public int blockCount() {
    return blocks.size();
  }
  
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);  
  }

  
  public void close(Tag...tags) {
  }
  
}
