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

import haruspex.common.Tag;

public interface ITraceElement {
  ITraceElement getParent();
  
  Iterator<Tag> getTagIterator();

  int getTagCount();
  
  default ITrace getTrace() {
    ITraceElement el; 
    for (el = this; !(el instanceof ITrace); el = el.getParent()) {      
    }
    return (ITrace)el;
  }
  
}
