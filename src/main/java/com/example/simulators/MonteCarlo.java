package com.example.simulators;

import com.example.simulators.MonteCarlo.MonteCarloResult;
import java.util.Arrays;
import java.util.Random;

/**
 * A class which can run many times monte carlo simulation for a given portfolio over future years.
 *
 * <p>The simulation will provide the top 10% percent best case and 10% worst case, in addition to
 * the mean value return.
 *
 * <p>The value of original portfolio is 1, and the overall return is computed based on the expected
 * rate and the inflation as following:
 *
 * <p>overall_return = (1+return_rate)/(1+inflation)
 *
 * <p>The return rate is a Gaussian distribution, with given Mean of return, and Standard deviation
 * (Risk).
 */
public class MonteCarlo implements ISimulator<MonteCarloResult> {

  private final double percent;
  private final int years;
  private final int trials;
  private final double mean;
  private final double sigma;
  private final double inflation;

  /**
   * The constructor.
   *
   * @param years Total years to investment
   * @param trials Total trials to run for the simulation, e.g., 10,000 trials means running
   *     simulation 10,000 times.
   * @param rate Mean value of expected return rate per year.
   * @param stdDeviation Standard deviation of the return rate (volatility, or risk)
   * @param inflation Inflation rate per year
   * @param percent the percentile value to return. e.g., if percent=10%, then return the 10%th and
   *     90%th value.
   */
  public MonteCarlo(
      int years, int trials, double rate, double stdDeviation, double inflation, double percent) {
    this.years = years;
    this.trials = trials;
    this.mean = rate;
    this.sigma = stdDeviation;
    this.inflation = inflation;
    this.percent = percent;
  }

  /**
   * Run simulation and return the rsult.
   *
   * @return an instance of MonteCarloResult as result.
   * @see MonteCarloResult
   */
  @Override
  public MonteCarloResult run() {
    long start = System.currentTimeMillis();

    Random random = new Random();

    // Future Value
    double[] fv = new double[trials];
    Arrays.fill(fv, 1.0);

    for (int j = 0; j < trials; j++) {
      for (int i = 1; i <= years; i++) {
        double ror = random.nextGaussian() * sigma + mean;
        fv[j] = fv[j] * (1 + ror) / (1 + inflation);
      }
    }

    Arrays.sort(fv);

    MonteCarloResult result =
        new MonteCarloResult(
            fv[(int) (trials * percent)],
            Arrays.stream(fv).average().getAsDouble(),
            fv[(int) (trials * (1 - percent))],
            System.currentTimeMillis() - start);
    return result;
  }

  /** This holds the result of MonteCarlo simulation. */
  public static class MonteCarloResult {
    private final double worst;
    private final double median;
    private final double best;
    private final long time;

    public MonteCarloResult(double worst, double median, double best, long time) {
      this.worst = worst;
      this.median = median;
      this.best = best;
      this.time = time;
    }

    public double getWorst() {
      return worst;
    }

    public double getMedian() {
      return median;
    }

    public double getBest() {
      return best;
    }

    public long getTime() {
      return time;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("time %d(ms):\n", getTime()));
      sb.append(String.format("  median: %6.4f\n", getMedian()));
      sb.append(String.format("  %s percent best case: %6.4f\n", "10%", getBest()));
      sb.append(String.format("  %s percent worst case: %6.4f\n", "10%", getWorst()));
      return sb.toString();
    }
  }
}
