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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import haruspex.batchdiviner.Diviner;
import haruspex.common.TraceEvents;
import haruspex.common.event.IEventSource;
import haruspex.model.BytesRecordSource;
import haruspex.model.StringsRecordSource;

public class Urn {
  public static final Options OPTIONS = new Options();
  public static final Properties PROPERTIES = new Properties();

  public static void main(String[] args) throws FileNotFoundException, IOException {
    OPTIONS.process(args);
    System.out.println("CONFIG = " + OPTIONS.config);
    PROPERTIES.load(new FileInputStream(OPTIONS.config));
    OPTIONS.override(PROPERTIES);

    String traceSourceClass = PROPERTIES.getProperty(BaseProperties.TRACE_SOURCE_CLASS);
    if (traceSourceClass == null) {
      fatalError("missing property " + BaseProperties.TRACE_SOURCE_CLASS + " from file " + OPTIONS.config);
    }
    String traceStorageClass = PROPERTIES.getProperty(BaseProperties.TRACE_STORAGE_CLASS);
    if (traceStorageClass == null) {
      fatalError("missing property " + BaseProperties.TRACE_STORAGE_CLASS + " from file " + OPTIONS.config);
    }
    int workerCount = Integer.parseInt(PROPERTIES.getProperty(BaseProperties.WORKER_COUNT, "8"));
    long pollPeriod = Integer.parseInt(PROPERTIES.getProperty(BaseProperties.TRACE_SOURCE_POLL_PERIOD, "1000"));
    ExecutorService executor = Executors.newFixedThreadPool(workerCount);    
    for (int i = 0; i < workerCount; i++) {
      executor.submit(() -> {
        IEventSource recordSource = newInstance(traceSourceClass);
        TraceStorage traceStorage = newInstance(traceStorageClass);
        try {
          if (recordSource instanceof StringsRecordSource) {
            StringsRecordSource stringRecordSource = (StringsRecordSource)recordSource;
            while(true) {
              TraceEvents<String>[] traceEvents = stringRecordSource.pollAsStrings(0);
              for (TraceEvents<String> ev : traceEvents) {
                traceStorage.storeStrings(ev);
              }
              recordSource.checkpoint();
              Thread.sleep(pollPeriod);
            }
          } else {
            BytesRecordSource bytesRecordSource = (BytesRecordSource)recordSource;
            while(true) {
              TraceEvents<byte[]>[] traceEvents = bytesRecordSource.pollAsBytes(0);
              for (TraceEvents<byte[]> ev : traceEvents) {
                traceStorage.storeBytes(ev);
              }
              recordSource.checkpoint();
              Thread.sleep(pollPeriod);
            }
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      });
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T newInstance(String className) {
    T instance = null;
    try {
      Class<?> clazz = Class.forName(className);
      instance = (T)clazz.newInstance();      
    } catch (ClassNotFoundException ex) {
      Diviner.fatalError("Class '" + className + "' not found in classpath");
    } catch (InstantiationException ex) {
      Diviner.fatalError("Unable to instantiate class '" + className + "':", ex);
    } catch (IllegalAccessException ex) {
      Diviner.fatalError("IllegalAccess to class '" + className + "':", ex);
    }     
    return instance;    
  }
  
  
  public static void fatalError(String msg) {
    System.err.println(msg);
    System.exit(1);
  }

  public static void fatalError(String msg, Throwable ex) {
    System.err.println(msg);
    ex.printStackTrace(System.err);
    System.exit(1);
  }

  static class Options {
    @Option(name = "--config", required = true, usage = "path of properties file")
    public String config;

    @Option(name = "--properties", handler = StringArrayOptionHandler.class, usage = "list of key=value properties")
    public List<String> props;

    public void process(String[] args) {
      CmdLineParser parser = new CmdLineParser(OPTIONS);
      try {
        parser.parseArgument(args);
      } catch (CmdLineException e) {
        System.err.println(e.getMessage());
        parser.printUsage(System.err);
        System.exit(1);
      }
    }

    public void override(Properties target) {
      if (props != null) {
        for (String kvPairStr : props) {
          String[] kvPair = kvPairStr.split("=");
          if (kvPair.length != 2) {
            Urn.fatalError("Invalid key=value argument to --properties: " + kvPairStr);
          }
          target.setProperty(kvPair[0], kvPair[1]);
        }
      }
    }
  }
}
