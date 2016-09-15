package haruspex.producer;

import java.io.PrintWriter;
import java.io.StringWriter;

import haruspex.common.CoreTags;
import haruspex.common.GlobalID;
import haruspex.model.ImmutableTrace;
import haruspex.model.MutableBlock;
import haruspex.model.MutableEntity;
import haruspex.model.MutableTrace;
import haruspex.producer.serialization.XML;

public class TestMutable {
  public static void main(String[] args) {
    test1();
  }
  
  private static void test1() {
    MutableTrace trace = new MutableTrace();
    MutableEntity e1 = trace.entity("e1");
    MutableBlock b1 = e1.block("b1");
    GlobalID id = GlobalID.random();
    b1.point("p1", 0, CoreTags.cause(id));
    
    MutableEntity e2 = trace.entity("e2");
    MutableBlock b2 = e2.block("b2", CoreTags.effect(id));
    
    ImmutableTrace iTrace = trace.toImmutable();
    
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    
    XML.serialize(pw, iTrace);
    System.out.println(sw.toString());
  }
}
