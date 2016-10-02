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
package haruspex.producer;

import haruspex.common.ID;
import haruspex.common.event.IEventSink;

/**
 * Common superclass to {@link Trace}, {@link Entity}, {@link Block}.
 * 
 */
abstract public class TraceElement {
  private final Trace trace;
  private final ID id;
  private final IEventSink sink;
  private final TraceElement parent;
  
  /**
   * creates a trace element.
   * @param trace the {@link Trace) object this model element belongs to, or {@code null} if this is 
   * in fact the {@code Trace} object.
   * @param parent the parent object for this one. 
   * @param id the ID for the object
   * @param sink the sink used for event generation.
   */
  TraceElement(Trace trace, TraceElement parent, ID id, IEventSink sink){
    this.trace = trace == null ? (Trace)this : trace;
    this.parent = parent;
    this.id = id;
    this.sink = sink;
  }
  
  /**
   * @return the object ID
   */
  public ID getID() {
    return id;
  }

  /**
   * 
   * @return the {@link IEventSink} for this object.
   */
  public IEventSink getSink() {
    return sink;
  }
  
  /**
   * @return the {@link Trace} instance this element belongs to or is.
   */
  public Trace getTrace() {
    return trace;
  }
  
  /**
   * @return the parent model element.
   */
  public TraceElement getParent() {
    return parent;
  }
}
