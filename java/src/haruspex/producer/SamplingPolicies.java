package haruspex.producer;

/**
 * This is a container for some common sampling policies
 * @author roberto
 *
 */
public class SamplingPolicies {
  
  /**
   * each trace created with this policy results into generation of records.
   */
  public static final SamplingPolicy ALWAYS = new SamplingPolicy() {
    public boolean shouldTrace() {
      return true;
    }
  };

  /*
   * traces created with this policy result in producing records with the specified probability in [0.0, 1.0]
   */
  public static final SamplingPolicy  WITH_PROBABILITY(double probability) {
    if (probability < 0.0 || probability > 1.0) {
      throw new IllegalArgumentException("Probability should be in [0.0-1.0] interval");
    }
    return new SamplingPolicy() {
      public boolean shouldTrace() {
        return Math.random() < probability;
      }
    };
  };
}
