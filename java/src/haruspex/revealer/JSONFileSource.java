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
package haruspex.revealer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import haruspex.common.event.ITraceEvent;
import haruspex.common.event.JSONEventCodec;
import haruspex.model.ImmutableTrace;

public class JSONFileSource implements ISource {
  private JFileChooser fc = new JFileChooser();

  @Override
  public ImmutableTrace load() throws IOException {
    int res = fc.showOpenDialog(null);
    if (res == JFileChooser.APPROVE_OPTION) {
      JSONEventCodec codec = new JSONEventCodec();
      ArrayList<ITraceEvent> events = new ArrayList<>();
      File f = fc.getSelectedFile();
      try (BufferedReader r = new BufferedReader(new FileReader(f))) {
        String line;
        while((line = r.readLine()) != null) {
          events.add(codec.decode(line));
        }
      }
      return ImmutableTrace.fromEvents(events);
    } else {
      return null;
    }
  }

}
