package com.example.simulation;

import static junit.framework.TestCase.assertTrue;

import com.example.simulation.MonteCarlo.MonteCarloResult;
import java.io.IOException;
import org.junit.Test;

public class MonteCarloTest {
  private static final double INFLATION = 0.035;
  private static final int N = 100000; // total trials
  private static final int YEARS = 20; // total years
  private static final double AGGRESSIVE_RETURN = 0.094324; // return mean
  private static final double AGGRESSIVE_RISK = 0.15675; // standard deviation
  private static final double CONSERVATIVE_RETURN = 0.06189; // return mean
  private static final double CONSERVATIVE_RISK = 0.063438; // standard deviation
  private static final double PERCENT = 0.1;

  @Test
  public void testSimulation() throws IOException {
    MonteCarloResult aggressive =
        new MonteCarlo(YEARS, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, PERCENT).run();
    System.out.println("Aggressive: " + aggressive);

    MonteCarloResult conservative =
        new MonteCarlo(YEARS, N, CONSERVATIVE_RETURN, CONSERVATIVE_RISK, INFLATION, PERCENT).run();
    System.out.println("Conservative: " + conservative);

    assertTrue(
        "Aggressive average return should great than Conservative",
        aggressive.getMedian() > conservative.getMedian());
    assertTrue(
        "Aggressive %10 best case should great than Conservative",
        aggressive.getBest() > conservative.getBest());
    assertTrue(
        "Aggressive %10 worst case should less than Conservative",
        aggressive.getWorst() < conservative.getWorst());
  }
}
