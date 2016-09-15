package haruspex.batchdiviner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class Diviner {
  public static final Options OPTIONS = new Options();
  public static final Properties PROPERTIES = new Properties();
  
  public static void main(String[] args) throws FileNotFoundException, IOException {
    OPTIONS.process(args);
    System.out.println("CONFIG = "+ OPTIONS.config);
    PROPERTIES.load(new FileInputStream(OPTIONS.config));
    OPTIONS.override(PROPERTIES);
    
    String traceSourceClass = PROPERTIES.getProperty(BaseProperties.TRACE_SOURCE_CLASS);
    if (traceSourceClass == null) {
      fatalError("missing property " + BaseProperties.TRACE_SOURCE_CLASS + " from file " + OPTIONS.config);
    }
    int workerCount = Integer.parseInt(PROPERTIES.getProperty(BaseProperties.WORKER_COUNT, "8"));
    String pythonPath = PROPERTIES.getProperty(BaseProperties.PYTHON_PATH);
    new DivinerMaster(workerCount, traceSourceClass, pythonPath);
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
    @Option(name="--config", required=true, usage="path of properties file")
    public String config;

    @Option(name="--properties", handler=StringArrayOptionHandler.class, usage="list of key=value properties")
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
            Diviner.fatalError("Invalid key=value argument to --properties: " + kvPairStr);
          }     
          target.setProperty(kvPair[0], kvPair[1]);
        }
      }
    }
  }
}
