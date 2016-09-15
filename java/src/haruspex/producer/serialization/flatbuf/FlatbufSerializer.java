package haruspex.producer.serialization.flatbuf;

import java.io.IOException;
import com.google.flatbuffers.FlatBufferBuilder;

import haruspex.common.ID;
import haruspex.common.TagList;
import haruspex.producer.BytesRecordSink;
import haruspex.producer.TraceSerializer;

public class FlatbufSerializer implements TraceSerializer {
  private final BytesRecordSink sink;
  
  public FlatbufSerializer(BytesRecordSink sink) throws IOException {
    this.sink = sink;
  }
  
  private int[] serializeTagLists(FlatBufferBuilder builder, TagList[] tags) {
    int tagListOff[] = new int[tags.length];
    int tloIdx = 0;
    for (TagList tl : tags) {
      int tagOff[] = new int[tl.size()];
      int idx = 0;
      for (haruspex.common.Tag tag : tl) {
        int key = builder.createString(tag.getKey());
        int value = builder.createString((String)tag.getValue().toString());
        tagOff[idx++] = Tag.createTag(builder, key, value);
      }
      int tagsOff = TagContext.createTagsVector(builder, tagOff);
      byte type;
      switch (tl.getContext()) {
      case TRACE:
        type = ContextType.TRACE;
        break;
      case ENTITY:
        type = ContextType.ENTITY;
        break;
      case BLOCK:
        type = ContextType.BLOCK;
        break;
      case POINT:
        type = ContextType.POINT;
        break;  
      default:
        throw new IllegalStateException();
      }
      tagListOff[tloIdx++] = TagContext.createTagContext(builder, type, tagsOff);      
    }
    return tagListOff;
  }
  
  
  @Override
  public void serialize(long seqNum, long timeStamp, ID[] context, TagList... tags) {
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
    sink.put(context[0], builder.sizedByteArray());
  }

  @Override
  public void flush() {
    sink.flush();
  }

  @Override
  public void close() {
    sink.close();
  }

}
