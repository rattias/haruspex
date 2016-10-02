package haruspex.common.event.flatbuf;

import com.google.flatbuffers.FlatBufferBuilder;

import haruspex.common.ID;
import haruspex.common.TagList;
import haruspex.common.event.CodecException;
import haruspex.common.event.IEventCodec;
import haruspex.common.event.ITraceEvent;

public class FlatbufCodec implements IEventCodec {
  
  private int[] serializeTagLists(FlatBufferBuilder builder, TagList[] tags) {
    int tagListOff[] = new int[tags.length];
    for (TagList tl : tags) {
      int tagOff[] = new int[tl.size()];
      int idx = 0;
      for (haruspex.common.Tag tag : tl) {
        int key = builder.createString(tag.getKey());
        int value = builder.createString((String)tag.getValue().toString());
        tagOff[idx++] = Tag.createTag(builder, key, value);
      }
    }
    return tagListOff;
  }
  
  
  @Override
  public String encode(long seqNum, long timeStamp, ID[] context, TagList... tags) {
    FlatBufferBuilder builder = new FlatBufferBuilder(0);
    int[] tagContexts = serializeTagLists(builder, tags);
    int tagsVector = TraceEvent.createTagsVector(builder, tagContexts);
    TraceEvent.startTraceEvent(builder);
    TraceEvent.addSeqNum(builder, seqNum);
    TraceEvent.addTimestamp(builder, timeStamp);
    TraceEvent.addTraceId(builder, context[0].toLong());
    if (context.length > 1) {
      TraceEvent.addEntityId(builder, context[1].toLong());
    }
    if (context.length > 2) {
      TraceEvent.addBlockId(builder, (int)context[2].toLong());
    }
    TraceEvent.addTags(builder, tagsVector);
    int root = TraceEvent.endTraceEvent(builder);
    builder.finish(root);
    return new String(builder.sizedByteArray());
  }


  @Override
  public ITraceEvent decode(String encodedEvent) throws CodecException {
    // TODO Auto-generated method stub
    return null;
  }
}
