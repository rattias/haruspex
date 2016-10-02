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

public class LocalID implements ID {
  public static final LocalID GHOST = new LocalID(-1);
  
  private long id;

  private LocalID(long id) {
    this.id = id;
  }
  
  public static LocalID of(long id) {
    return new LocalID(id);
  }
  
  public long toLong() {
    return id;
  }
  
  public String toString() {
    return Long.toHexString(id);
  }
  
  @Override
  public int hashCode() {
    return (int)toLong();
  }
  
  @Override
  public boolean equals(Object o) {
    return (o instanceof LocalID) && ((LocalID) o).id == id;
  }
}

