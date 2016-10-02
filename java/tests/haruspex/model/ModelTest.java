package haruspex.model;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.event.ITraceEvent;
import haruspex.common.event.InMemoryStorageSink;
import haruspex.producer.Block;
import haruspex.producer.Entity;
import haruspex.producer.Trace;

public class ModelTest {

  @Test
  public void testTopoSort() {
    InMemoryStorageSink sink = new InMemoryStorageSink();
    Trace t = new Trace.Builder().setSink(sink).build();
    Entity producer = t.entity("client");
    Block b1 = producer.block();
    b1.point(Tag.name("p1"));
    ID request =  GlobalID.random();
    b1.point(Tag.name("p2"), Tag.cause(request));

    Entity consumer = t.entity("server");
    Block b2 = consumer.block();
    b2.point(Tag.name("c1"));
    b2.point(Tag.name("c2"), Tag.effect(request));
    ID response =  GlobalID.random();
    b2.close(Tag.NONE, Tag.cause(response));

    b1.close(Tag.NONE, Tag.effect(response));

    List<ITraceEvent> events = sink.getEvents(t.getID());
    ImmutableTrace trace = ImmutableTrace.fromEvents(events);
    assertEquals(t.getID(), trace.getID());
    assertEquals(2, trace.getEntityCount());
    ImmutableEntity[] ents = new ImmutableEntity[2];
    for (Iterator<ImmutableEntity> it = trace.getEntityIterator(); it.hasNext();) {
      ImmutableEntity en = it.next();
      if (en.getName().equals("client")) {
        ents[0] = en;
      } else if (en.getName().equals("server")) {
        ents[1] = en;
      }
    }
    assertEquals("client", ents[0].getName());
    assertEquals("server", ents[1].getName());
    for (int i = 0; i < 2; i++) {
      assertEquals(1, ents[i].getBlockCount());
      ImmutableBlock b = ents[i].getBlockIterator().next();
      assertEquals(4, b.pointCount());
    }    
  }
  
  @Test
  public void testTags() {  
    InMemoryStorageSink sink = new InMemoryStorageSink();
    Trace t = new Trace.Builder().setSink(sink).build(Tag.of("t0", "v0"));
    Entity ent = t.entity("ent", Tag.of("t1", "v1"));
    Block bl = ent.block(new Tag[]{Tag.of("t2", "v2")}, Tag.of("t3", "v3"));
    bl.point(Tag.of("t4", "v4"));
    bl.close(new Tag[]{Tag.of("t5", "v5")}, Tag.of("t6", "v6"));
    List<ITraceEvent> events = sink.getEvents(t.getID());
    ImmutableTrace trace = ImmutableTrace.fromEvents(events);
    assertEquals("v0", trace.getTagValue("t0"));
    ImmutableEntity ment = trace.getEntityIterator().next();
    assertEquals("v1", ment.getTagValue("t1"));
    ImmutableBlock mbl = ment.getBlockIterator().next();
    assertEquals("v2", mbl.getTagValue("t2"));
    assertEquals("v3", mbl.getBegin().getTagValue("t3"));
    assertEquals("v4", mbl.getPointAt(1).getTagValue("t4"));
    assertEquals("v5", mbl.getTagValue("t5"));
    assertEquals("v6", mbl.getEnd().getTagValue("t6"));    
  }
}
