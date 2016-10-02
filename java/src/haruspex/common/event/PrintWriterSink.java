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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haruspex.common.ID;
import haruspex.common.TagList;

public class PrintWriterSink implements IEventSink {
  private final static Logger logger = LoggerFactory.getLogger(PrintWriterSink.class);
  private final PrintWriter pw;
  private final IEventCodec codec;

  public PrintWriterSink(PrintWriter pw) {
    this(pw, new JSONEventCodec());
  }
  
  public PrintWriterSink(PrintWriter pw, IEventCodec codec) {
    this.pw = pw;
    this.codec = codec;
  }
  
  @Override
  public void flush() {
    pw.flush();
  }
  
  public void close() {
    pw.close();
  }
  
  public boolean isLossy() {
    return false;
  }

  @Override
  public void put(long seqNum, long timeStamp, ID[] context, TagList... tags) {
    try {
      pw.println(codec.encode(seqNum, timeStamp, context, tags));
      pw.flush();
    } catch (CodecException ex) {
      logger.error("Error while encoding event", ex);
    }
  }
 
}
