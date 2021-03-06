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

import haruspex.common.Tag;
import haruspex.common.event.FileStorageSink;
import haruspex.producer.Block;
import haruspex.producer.Entity;
import haruspex.producer.Trace;

class HelloWorld2 {
  public static void main(String[] args) {
    Entity en = new Trace.Builder().setSink(new FileStorageSink("/tmp/traces")).build().entity("main");
    try (Block b = en.block(Tag.of(Tag.name("main")), Tag.of("foo", "bar"))) {
      System.out.println("Hello world!");
    }
  }
}
