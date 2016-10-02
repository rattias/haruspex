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

import haruspex.common.event.IEventSink;

/**
 * When a trace is created a sampling policy associated to it decides whether the various producer API
 * calls for this trace (and all of its sub-elements) should result in records generated onto the 
 * {@link IEventSink} and {@link RecordSink}.
 * 
 */
public interface SamplingPolicy {
  boolean shouldTrace();
}
