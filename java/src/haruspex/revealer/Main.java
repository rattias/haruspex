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
import java.io.IOException;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class Main {
  static class Options {
    @Option(name = "--config", usage = "path of properties file")
    public String config;

    @Option(name = "--sources", handler = StringArrayOptionHandler.class, usage = "list of sources classname ")
    public List<String> props;
    
    public void process(String[] args) {
      CmdLineParser parser = new CmdLineParser(this);
      try {
        parser.parseArgument(args);
      } catch (CmdLineException e) {
        System.err.println(e.getMessage());
        parser.printUsage(System.err);
        System.exit(1);
      }
    }
  }
  
  public static final Options OPTIONS = new Options();
  
  public static void main(String[] args) throws IOException {
    OPTIONS.process(args);
    JSONFileSource fs = new JSONFileSource();
    fs.load();
  }
}