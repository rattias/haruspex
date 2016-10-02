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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import haruspex.common.ID;
import haruspex.common.TagList;

public class FileStorageSink implements IEventSink {
  private final static Logger logger = LoggerFactory.getLogger(FileStorageSink.class);
  private final static int MAX_OPEN_FILES = 64;
  
  private final File dirFile;
  private final LinkedHashMap<ID, PrintWriter> openFiles;
  private final IEventCodec codec;
  
  public FileStorageSink(String dirPath) {
    this(dirPath, new JSONEventCodec());
  }
  
  public FileStorageSink(String dirPath, IEventCodec codec) {
    dirFile = new File(dirPath);
    this.codec = codec;
    openFiles = new LinkedHashMap<ID, PrintWriter>(MAX_OPEN_FILES, .75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<ID, PrintWriter> eldest) {
        if (size() >= MAX_OPEN_FILES) {
          remove(eldest.getKey());
          eldest.getValue().close();
        }
        return false;
      }
    };
    if (dirFile.exists() && !dirFile.isDirectory()) {
      throw new IllegalArgumentException("File \"" + dirPath + "\" exists, but it's not a directory.");
    }
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }    
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        close();
      }
    });
  }

  @Override
  public void put(
    long seqNum,
    long timeStamp,
    ID[] ids,
    TagList...tags
    ) {
    PrintWriter pw = getWriter(ids[0]);
    try {
      String ev = codec.encode(seqNum, timeStamp, ids, tags);
      pw.println(ev);
    } catch (CodecException ex) {
      logger.error("Error while encoding event:", ex);
    }
  }
  
  public void flush() {
    for (Iterator<Map.Entry<ID, PrintWriter>> it = openFiles.entrySet().iterator(); it.hasNext();) {
      Map.Entry<ID, PrintWriter> entry = it.next();
      entry.getValue().flush();
    }
  }
  
  public void close() {
    for (Iterator<Map.Entry<ID, PrintWriter>> it = openFiles.entrySet().iterator(); it.hasNext();) {
      Map.Entry<ID, PrintWriter> entry = it.next();
      it.remove();
      entry.getValue().close();
    }
  }
  
  private synchronized PrintWriter getWriter(ID traceID) {
    PrintWriter pw = openFiles.get(traceID);
    if (pw == null) {
      try {
        String fname = traceID.toString().replace("/", "_");
        pw = new PrintWriter(new FileWriter(new File(dirFile, fname), true));
      } catch (IOException ex) {
        logger.error("Error while creating PrintWriter/FileWriter:", ex);
      }
      openFiles.put(traceID, pw);
    }
    return pw;    
  }
  

}
