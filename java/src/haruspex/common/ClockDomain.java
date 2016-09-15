package haruspex.common;

import java.util.concurrent.TimeUnit;

public interface ClockDomain {
    public long getTime();
  
  public TimeUnit getTimeUnit();
  
  public String getName();
  
  public long getMaxSkew();
}
