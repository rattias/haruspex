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
package haruspex.urn;

import java.io.IOException;

import haruspex.common.ID;
import haruspex.common.TraceEvents;

public interface TraceStorage {
  void storeBytes(TraceEvents<byte[]> events) throws IOException;
  void storeStrings(TraceEvents<String> events) throws IOException;
  TraceEvents<byte[]> loadBytes(ID traceId) throws IOException;
  TraceEvents<String> loadStrings(ID traceId) throws IOException;
}
