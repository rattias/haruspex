package haruspex.batchdiviner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import haruspex.model.ImmutableTrace;

import static java.nio.file.StandardWatchEventKinds.*;

public class PathWatcher { 
  private List<PathChangeListener> listeners;
  private WatchService watcher;
  private Map<WatchKey, Path> watchKeysToDir = new HashMap<>();
  private final long updatePeriod;
  private final Thread watcherThread;
  private volatile boolean timerArmed;
  private String[] pathEntries;
  private Consumer<String[]> consumer;
  
  public PathWatcher(String path) {
    try {
      watcher = FileSystems.getDefault().newWatchService();
    } catch (IOException ex) {
      Diviner.fatalError("Error creating watch service", ex);
    }
    updatePeriod = Long.parseLong(Diviner.PROPERTIES.getProperty("python.updateperiod", "5000"));
    
    pathEntries = path.split(File.pathSeparator);
    
    final Timer timer = new Timer();
    
    watcherThread = new Thread() {
      @SuppressWarnings("unchecked")
      public void run() {
        long lastUpdate = System.currentTimeMillis();
        for (;;) {
          // wait for key to be signaled
          WatchKey key;
          try {
              key = watcher.take();
          } catch (InterruptedException x) {
              return;
          }         
          if (!timerArmed) {
            long current = System.currentTimeMillis();
            timerArmed = true;
            timer.schedule(
                new TimerTask() {
                  public void run() {
                    timerArmed = false;
                    notifyListeners();
                  }
                }, 
                Math.max(updatePeriod - current + lastUpdate, 0)
             );
            lastUpdate = current;
          }
          boolean valid = key.reset();
          if (!valid) {
            break;
          }
        }
      }
    };
  }
  
  public void addListener(PathChangeListener l) {
    listeners.add(l);
  }
  
  
  private void notifyListeners() {
    for (PathChangeListener l : listeners) {
      l.pathChanged(pathEntries);
    }
  }
  
  public void start() {
    notifyListeners();
    watcherThread.start();
  }
  
}
