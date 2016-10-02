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
package haruspex.common;

import java.util.Base64;
import java.util.Random;

public class GlobalID implements ID {
  public final static GlobalID GHOST = new GlobalID(-1);
  
  private final static Random random = new Random();
  private final static Base64.Encoder b64enc = Base64.getEncoder().withoutPadding();
  private final static Base64.Decoder b64dec = Base64.getDecoder();
  private long id;

  /* TODO: cache by id */
  public static GlobalID of(long id) {
    return new GlobalID(id);
  }

  public static GlobalID of(String idStr) {
    byte[] bytes = b64dec.decode(idStr);
    long id = 0;
    for (int i = 0; i < 8; i++) {
      id |= (((long)bytes[i]) & 0xFF) << (i * 8);
    }
    return new GlobalID(id);
  }

  private GlobalID(long id) {
    this.id = id;
  }
  
  @Override
  public long toLong() {
    return id;
  }
  
  public String toString() {
    byte[] b = new byte[8];
    for (int i = 0; i < 8; i++) {
      b[i] = (byte)((id >>> (i * 8)) & 0xFF);
    }
    return b64enc.encodeToString(b);
  }
  
  public static GlobalID random() {
    return new GlobalID(random.nextLong());
  }
  
  @Override
  public boolean equals(Object o) {
    return (o instanceof GlobalID) && ((GlobalID) o).id == id;
  }

  @Override
  public int hashCode() {
    return (int)toLong();
  }
}

