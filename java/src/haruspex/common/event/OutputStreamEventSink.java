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

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haruspex.common.ID;
import haruspex.common.TagList;

public class OutputStreamEventSink implements IEventSink {
  private final static Logger logger = LoggerFactory.getLogger(OutputStreamEventSink.class);
  private final OutputStream os;
  private final IEventCodec codec;
  
  public OutputStreamEventSink(OutputStream os) {
    this(os, new JSONEventCodec());
  }

  public OutputStreamEventSink(OutputStream os, IEventCodec codec) {
    this.os = os;
    this.codec = codec;
  }

  @Override
  public void flush() {
    try {
      os.flush();
    } catch (IOException ex) {
      logger.error("Error while flushing sink", ex);
    }
  }
  
  @Override
  public void close() {
    try {
      os.close();
    } catch (IOException ex) {
      logger.error("Error while closing sink", ex);
    }
  }
  
  public boolean isLossy() {
    return false;
  }

  @Override
  public void put(long seqNum, long timeStamp, ID[] context, TagList... tags) {
    try {
      os.write(codec.encode(seqNum, timeStamp, context, tags).getBytes());
    } catch (IOException ex) {
      logger.error("Error while writing to sink", ex);
    }
  }
  

}
