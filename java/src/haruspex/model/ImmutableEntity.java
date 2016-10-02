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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import haruspex.common.GlobalID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;

public class ImmutableEntity extends ImmutableTraceElementWithID implements IEntity {
  private int blockIdCounter;
  private Map<LocalID, ImmutableBlock> blocks = new LinkedHashMap<>();
  

  ImmutableEntity(ImmutableTrace trace, String name, GlobalID id, Tag...tags) {
    super(id, trace, tags);
    addAll(Tag.name(name));
  }

  ImmutableEntity(ImmutableTrace trace, GlobalID id) {
    super(id, trace);
  }

  public String getName() {
    return (String)getTagValue(Tag.NAME);
  }
 
  ImmutableBlock block(Tag...blockTags) {
    return block(blockTags, TagList.EMPTY_ARRAY);
  }

  ImmutableBlock block(Tag[] blockTags, Tag...pointTags) {
    return block(LocalID.of(blockIdCounter++), blockTags, pointTags);
  }
 
  ImmutableBlock block(LocalID id, Tag[] blockTags, Tag...pointTags) {
    ImmutableBlock block = new ImmutableBlock(id, getTrace(), this, blockTags, pointTags);
    blocks.put(id, block);
    return block;
  }
  
  @Override
  public Iterator<ImmutableBlock> getBlockIterator() {
    return blocks.values().iterator();
  }
  
  public ImmutableBlock getBlock(LocalID id) {
    return blocks.get(id);
  }

  @Override
  public int getBlockCount() {
    return blocks.size();
  }
  
  @Override
  public int hashCode() {
    long l = getID().toLong();
    return (int)((l >> 32) | l) & 0xFFFFFFFF;
  }
  
  @Override
  public boolean equals(Object o) {
    return (o instanceof ImmutableEntity) && ((ImmutableEntity)o).getID().equals(getID());
  }
}
