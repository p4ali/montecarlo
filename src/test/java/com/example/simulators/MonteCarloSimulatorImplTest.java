package com.example.simulators;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

import com.example.simulators.MonteCarloSimulatorImpl.MonteCarloResult;
import java.io.IOException;
import org.junit.Test;

public class MonteCarloSimulatorImplTest {
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
        new MonteCarloSimulatorImpl(
                YEARS, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, PERCENT)
            .run();
    System.out.println("Aggressive: " + aggressive);

    MonteCarloResult conservative =
        new MonteCarloSimulatorImpl(
                YEARS, N, CONSERVATIVE_RETURN, CONSERVATIVE_RISK, INFLATION, PERCENT)
            .run();
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

  @Test
  public void testNoNegativeYears() {
    try {
      new MonteCarloSimulatorImpl(-10, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, PERCENT);
      fail("Failed to catch negative Years!");
    } catch (InvalidSimulationParameterExcetption e) {
      assertTrue(e.getMessage().contains("Years"));
    }
  }

  @Test
  public void testNoNegativeTrials() {
    try {
      new MonteCarloSimulatorImpl(
          YEARS, -N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, PERCENT);
      fail("Failed to catch negative Trials!");
    } catch (InvalidSimulationParameterExcetption e) {
      assertTrue(e.getMessage().contains("Trials"));
    }
  }

  @Test
  public void testNoNegativePercentage() {
    try {
      new MonteCarloSimulatorImpl(
          YEARS, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, -PERCENT);
      fail("Failed to catch negative Percentage!");
    } catch (InvalidSimulationParameterExcetption e) {
      assertTrue(e.getMessage().contains("Percent"));
    }
  }

  @Test
  public void testGreaterThanHundredPercentage() {
    try {
      new MonteCarloSimulatorImpl(
          YEARS, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, 1 + PERCENT);
      fail("Failed to catch Percentage greater than 1!");
    } catch (InvalidSimulationParameterExcetption e) {
      assertTrue(e.getMessage().contains("Percent"));
    }
  }
}
