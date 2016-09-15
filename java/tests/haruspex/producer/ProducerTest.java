package haruspex.producer;

import java.io.PrintWriter;

import haruspex.common.CoreTags;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.producer.Block;
import haruspex.producer.Entity;
import haruspex.producer.Trace;
import haruspex.producer.TraceSerializer;
import haruspex.producer.serialization.JsonSerializer;
import haruspex.producer.serialization.PrintWriterSink;

public class ProducerTest {
  
  static void test1(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "a block closed with try-with-resource"));
    Entity e = t.entity("entity", Tag.of("e1", "ev1"), Tag.of("e2", 2));
    try (Block b = e.block("block", Tag.of("b3", "bv3"), Tag.of("b4", 4L))) {
      b.point("local_event");
    }
  }
  
  static void test2(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "same as before, but with block and first point annotated"));
    Entity e = t.entity("entity", Tag.of("e1", "ev1"), Tag.of("e2", 2));
    try (Block b = e.block("block", new Tag[]{Tag.of("b3", "bv3")}, new Tag[]{Tag.of("p4", 4L)})) {
    }
  }
  

  
  static void test3(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "a block closed as part of a cause"));
    Entity e = t.entity("entity");
    Block b = e.block("block");
    b.point("local_event", CoreTags.endBlock(), Tag.of("tag5", 0x12));  
  }

  static void test4(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "two local entities with an interaction"));
    Entity sender = t.entity("sender");
    Block senderBlock = sender.block("dostuff_and_send", Tag.NONE, CoreTags.name("begin_point"));
    ID causeId = GlobalID.random();
    senderBlock.point("send", CoreTags.cause(causeId), CoreTags.endBlock(), Tag.of("tag5", 0x12));  
    // traceID and causeID are serialized and sent to receiver here
    Entity receiver = t.entity("receiver");
    try (Block receiverBlock = receiver.block("receive_and_do_stuff", Tag.NONE, CoreTags.name("receive"))) {
      receiverBlock.point("do_stuff");
    }
  }
}
