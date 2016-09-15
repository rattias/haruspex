package haruspex.common;

import java.util.concurrent.TimeUnit;

public class DefaultClockDomain implements ClockDomain {
  public final static ClockDomain INSTANCE = new DefaultClockDomain();
  
  private DefaultClockDomain() {
   
  }
  
  @Override
  public long getTime() {
    return System.nanoTime();
  }
  
  @Override
  public TimeUnit getTimeUnit() {
    return TimeUnit.NANOSECONDS;
  }
  
  @Override
  public String getName() {
    return "default";
  }
  
  public long getMaxSkew() {
    return 0L;
  }
}
