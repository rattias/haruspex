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
package haruspex.model;

import haruspex.common.Tag;

public class ImmutablePoint extends  ImmutableTraceElement implements IPoint {
  private long timestamp;
  
  ImmutablePoint(ImmutableBlock block, long timestamp, Tag...tags) {
    super(block, tags);
    getTrace().addPoint(this);
    this.timestamp = timestamp;
  }
  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  @Override
  public long timestamp() {
    return timestamp;
  }
}
