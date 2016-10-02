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
package haruspex.common.event;

import java.io.PrintWriter;
import java.util.Iterator;

import haruspex.common.Tag;
import haruspex.model.IBlock;
import haruspex.model.IEntity;
import haruspex.model.IPoint;
import haruspex.model.ITrace;
import haruspex.model.ITraceElement;

public class XML {
  private final static String spaces = "                                                  ";
  
  private static String indent(int in) {
    return spaces.substring(0, Math.min(spaces.length(), in*2));
  }
  
  private static void serializeTags(PrintWriter pw, ITraceElement el, int in) {
    if (el.getTagCount() > 0) {
      pw.println(indent(in) + "<tags>");
      for(Iterator<Tag> it = el.getTagIterator(); it.hasNext();) {
        Tag tag = it.next();
        pw.println(indent(in+1) + "<tag key=\"" + tag.getKey() + "\", value=\"" + tag.getValue()+"\"/>");             
      }
      pw.println(indent(in) + "</tags>");
    }
  }
    
  private static void serializeEntity(PrintWriter pw, IEntity en, int in) {
    pw.println(indent(in) + "<entity ID=\"" + en.getID() + "\">");
    serializeTags(pw, en, in+1);
    if (en.getBlockCount() > 0) {
      pw.println(indent(in+1) + "<blocks>");
      for (Iterator<? extends IBlock> it = en.getBlockIterator(); it.hasNext();) {
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
    if (trace.getEntityCount() > 0) {
      pw.println(indent(1) + "<entities>");
      for (Iterator<? extends IEntity> it = trace.getEntityIterator(); it.hasNext();) {
        serializeEntity(pw, it.next(), 2);
      }
      pw.println(indent(1) + "</entities>");
    }
    pw.println("</trace>");
  }
}
