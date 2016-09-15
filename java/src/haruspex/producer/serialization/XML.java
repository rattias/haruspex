package haruspex.producer.serialization;

import java.io.PrintWriter;
import java.util.Iterator;

import haruspex.common.Tag;
import haruspex.model.IBlock;
import haruspex.model.IEntity;
import haruspex.model.IPoint;
import haruspex.model.ITrace;
import haruspex.model.ITraceElement;
import haruspex.producer.Entity;

public class XML {
  private final static String spaces = "                                                  ";
  
  private static String indent(int in) {
    return spaces.substring(0, Math.min(spaces.length(), in*2));
  }
  
  private static void serializeTags(PrintWriter pw, ITraceElement el, int in) {
    if (el.tagCount() > 0) {
      pw.println(indent(in) + "<tags>");
      for(Iterator<Tag> it = el.tags(); it.hasNext();) {
        Tag tag = it.next();
        pw.println(indent(in+1) + "<tag key=\"" + tag.getKey() + "\", value=\"" + tag.getValue()+"\"/>");             
      }
      pw.println(indent(in) + "</tags>");
    }
  }
    
  private static void serializeEntity(PrintWriter pw, IEntity en, int in) {
    pw.println(indent(in) + "<entity ID=\"" + en.getID() + "\">");
    serializeTags(pw, en, in+1);
    if (en.blockCount() > 0) {
      pw.println(indent(in+1) + "<blocks>");
      for (Iterator<? extends IBlock> it = en.blocks(); it.hasNext();) {
        serializeBlock(pw, it.next(), in+2);
      }
      pw.println(indent(in+1) + "</blocks>");
    }
    pw.println(indent(in) + "</entity>");
  }
  
  private static void serializeBlock(PrintWriter pw, IBlock block, int in) {
    pw.println(indent(in) + "<block ID=\"" + block.getID() + "\">");
    serializeTags(pw, block, in+1);
    if (block.pointCount() > 0) {
      pw.println(indent(in+1) + "<points>");
      for (Iterator<? extends IPoint> it = block.points(); it.hasNext();) {
        serializePoint(pw, it.next(), in+2);
      }
      pw.println(indent(in+1) + "</blocks>");
    }
    pw.println(indent(in) + "</block>");
  }
  
  private static void serializePoint(PrintWriter pw, IPoint point, int in) {
    pw.println(indent(in) + "<point time=" + point.timestamp() + ">");
    serializeTags(pw, point, in+1);
    pw.println(indent(in) + "</point>");
  }
  
  public static void serialize(PrintWriter pw, ITrace trace) {
    pw.println("<trace ID=\"" + trace.getID() + "\">");
    serializeTags(pw, trace, 1);    
    if (trace.entityCount() > 0) {
      pw.println(indent(1) + "<entities>");
      for (Iterator<? extends IEntity> it = trace.entities(); it.hasNext();) {
        serializeEntity(pw, it.next(), 2);
      }
      pw.println(indent(1) + "</entities>");
    }
    pw.println("</trace>");
  }
}
