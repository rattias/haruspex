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
package haruspex.batchdiviner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

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
