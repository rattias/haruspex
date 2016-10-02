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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import haruspex.common.ID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public class ImmutableBlock extends ImmutableTraceElementWithID implements AutoCloseable, IBlock {
  private boolean closed = false;
  private final List<Integer> pointIdx = new ArrayList<>();
  
  ImmutableBlock(
      ID id, 
      ImmutableTrace trace, 
      ImmutableEntity entity, 
      Tag[] blockTags, 
      Tag...pointTags) {
    super(id, entity, blockTags);
    point(0, pointTags);
  }

  
  public ImmutablePoint point(long timestamp, Tag...tags) {
    if (closed) {
      throw new IllegalStateException("block was previously closed");
    }
    ImmutablePoint p = new ImmutablePoint(this, timestamp, tags);
    int index = getTrace().addPoint(p);
    pointIdx.add(index);
    return p;
  }

  @Override
  public Iterator<ImmutablePoint> points() {
    return new Iterator<ImmutablePoint>() {
      Iterator<Integer> it = pointIdx.iterator();
      
      @Override
      public boolean hasNext() {        
        return it.hasNext();
      }

      @Override
      public ImmutablePoint next() {
        return getTrace().getPointAt(it.next());
      }      
    };
  }
  
  @Override
  public ImmutablePoint getPointAt(int idx) {
    return getTrace().getPointAt(pointIdx.get(idx));
  }
  
  @Override
  public int pointCount() {
    return pointIdx.size();
  }
  
  @Override
  public void close() {
    close(TagList.EMPTY_ARRAY);  
  }

  public void close(Tag[] blockTags, Tag...pointTags) {
    addAll(blockTags);
    point(0, pointTags);
    closed = true;
  }


  public ImmutablePoint getBegin() {
    return getPointAt(0);
  } 

  public ImmutablePoint getEnd() {
    return getPointAt(pointCount()-1);
  } 

}

