package haruspex.producer;

import static org.junit.Assert.*;

import org.junit.Test;

import haruspex.common.EventType;
import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;
import haruspex.common.event.IEventSink;

public class SinkTest {
  private final static String KEY = "key";
  private final static String VALUE = "value";
  private int count = 0;
  @Test
  public void test() {
    IEventSink sink = new IEventSink() {
      private long prevSeqNum = -1;
      private ID traceID, entID, blockID;

      @Override
      public void put(long seqNum, long timeStamp, ID[] ids, final TagList... tags) {
        count++;
        if (prevSeqNum != -1) {
          assertEquals(seqNum, prevSeqNum + 1);
        }
        prevSeqNum = seqNum;
        if (traceID == null) {
          traceID = ids[0]; 
        } else {
          assertEquals(ids[0], traceID);
        }
        if (ids.length > 1) {
          if (entID == null) {
            entID = ids[1];
          } else {
            assertEquals(ids[1], entID);
          }
        }
        if (ids.length > 2) {
          if (blockID == null) {
            blockID = ids[2];
          } else {
            assertEquals(ids[2], blockID);
          }
        } 
        switch (count) {
        case 1:
          assertEquals(1, ids.length);
          break;
        case 2:
          assertEquals(2, ids.length);
          assertEquals(1, tags.length);
          assertNotNull(tags[0].get(KEY));
          break;
        case 3:
          assertEquals(3, ids.length);
          assertEquals(2, tags.length);
          assertNotNull(tags[0].get(KEY));
          assertNotNull(tags[0].get(EventType.BEGIN_BLOCK.getTag().getKey()));
          assertNotNull(tags[1].get(EventType.ANNOTATE_POINT.getTag().getKey()));
          break;
        case 4:
          assertEquals(3, ids.length);
          assertEquals(2, tags.length);
          assertNotNull(tags[0].get(EventType.END_BLOCK.getTag().getKey()));
          assertNotNull(tags[1].get(EventType.ANNOTATE_POINT.getTag().getKey()));
          break;
        }
      }
      
      @Override
      public void flush() {}
      
      @Override
      public void close() {}
    };
    Trace.Builder builder = new Trace.Builder().setSink(sink);
    Trace trace = builder.build();
    Entity en = trace.entity("entity", Tag.of(KEY, VALUE));
    Block bl = en.block(Tag.of(KEY, VALUE));
    bl.close();
    assertEquals(count, 4);
  }

}
