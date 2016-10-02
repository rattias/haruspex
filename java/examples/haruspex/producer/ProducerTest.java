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

import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.Tag;

public class ProducerTest {
  
  static void test1(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "a block closed with try-with-resource"));
    Entity e = t.entity("entity", Tag.of("e1", "ev1"), Tag.of("e2", 2));
    try (Block b = e.block(Tag.name("block"), Tag.of("b3", "bv3"), Tag.of("b4", 4L))) {
      b.point(Tag.name("local_event"));
    }
  }
  
  static void test2(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "same as before, but with block and first point annotated"));
    Entity e = t.entity("entity", Tag.of("e1", "ev1"), Tag.of("e2", 2));
    try (Block b = e.block(
        Tag.of(Tag.name("block"), Tag.of("b3", "bv3")), 
        Tag.of("p4", 4L)
        )) {
    }
  }
  
  static void test3(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "a block closed as part of a cause"));
    Entity e = t.entity("entity");
    Block b = e.block(Tag.name("block"));
    b.point(Tag.name("local_event"), Tag.of("tag5", 0x12));
    b.close();
  }

  static void test4(Trace.Builder builder) {
    Trace t = builder.build(Tag.of("DESCRIPTION", "two local entities with an interaction"));
    Entity sender = t.entity("sender");
    Block senderBlock = sender.block(new Tag[]{Tag.name("dostuff_and_send")}, Tag.name("begin_point"));
    ID causeId = GlobalID.random();
    senderBlock.close(Tag.NONE, Tag.cause(causeId), Tag.of("tag5", 0x12));  
    // traceID and causeID are serialized and sent to receiver here
    Entity receiver = t.entity("receiver");
    try (Block receiverBlock = receiver.block(
        Tag.of(Tag.name("receive_and_do_stuff")),
        Tag.effect(causeId), Tag.name("receive"))) {
      receiverBlock.point(Tag.name("do_stuff"));
    }
  }
}
