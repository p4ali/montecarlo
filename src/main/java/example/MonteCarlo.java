package example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

/**
 *  A class which can run > 100000 times monte carlo simulation for a given portfolio over future years.
 *
 *  <p/>
 *
 *  The simulation will provide the top 10% percent best case and 10% worst case, in addition to
 *  the mean value return.
 *
 *  <p/>
 *
 *  The value of original portfolio is 1, and the overall return is computed based on the expected rate
 *  and the inflation as following:
 *
 *  <p/>
 *
 *  overall_return = (1+return_rate)/(1+inflation)
 *
 *  <p/>
 *
 *  The return rate is a Gaussian distribution, with given Mean of return, and Standard deviation (Risk).
 */
public class MonteCarlo {

  /**
   * Run monte carlo simulation and return the result. If path is not null, save the result to the
   * corresponding file.
   *
   * @param years total years to investment
   * @param trials total trials to simulate
   * @param mean the mean of expected return rate
   * @param sigma the standard deviation
   * @param inflation the inflation rate
   * @return An array contains median, 10% best case, 10% worst case, runtime(ms)
   */
  private double[] simulateAndSaveResult(
      int years, int trials, double mean, double sigma, double inflation, Path path) {
    long start = System.currentTimeMillis();

    Random random = new Random();

    // Future Value
    double[][] fv = new double[years + 1][trials];
    for (int j = 0; j < trials; j++) {
      fv[0][j] = 1.0;
    }

    for (int j = 0; j < trials; j++) {
      for (int i = 1; i <= years; i++) {
        double ror = random.nextGaussian() * sigma + mean;
        fv[i][j] = fv[i - 1][j] * (1 + ror) / (1 + inflation);
        // System.out.printf("% 10.4f, %10.4f\n", ror * 100, fv[i][j]);
      }
    }

    // min, mean, max, time
    double[] result = new double[4];
    double[] lastRow = new double[trials];
    System.arraycopy(fv[years], 0, lastRow, 0, trials);
    Arrays.sort(lastRow);

    for (int j = 0; j < lastRow.length; j++) {
      result[0] += lastRow[j];
    }

    result[0] /= trials;
    result[1] = lastRow[(int) (trials * 0.9)];
    result[2] = lastRow[(int) (trials * 0.1)];
    result[3] = System.currentTimeMillis() - start;

    if (path != null) postProcess(fv, path);

    return result;
  }

  public double[] simulateAndSave(
      int years, int trials, double mean, double sigma, double inflation, Path path) {
    return simulateAndSaveResult(years, trials, mean, sigma, inflation, path);
  }

  public double[] simulate(int years, int trials, double mean, double sigma, double inflation) {
    return simulateAndSaveResult(years, trials, mean, sigma, inflation, null);
  }

  private void postProcess(double[][] fv, Path path) {

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < fv.length; i++) {
        sb.setLength(0);
        sb.append(String.format("%2d", i));
        for (int j = 0; j < fv[0].length; j++) {
          sb.append(String.format(",% 10.4f", fv[i][j]));
        }
        sb.append("\n");
        writer.write(sb.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void print(double[] result, String name) {
    System.out.printf("%s(%dms):\n", name.toUpperCase(), (long) result[3]);
    System.out.printf("  median: %6.4f\n", result[0]);
    System.out.printf("  %s best case: %6.4f\n", "10%", result[1]);
    System.out.printf("  %s percent worst case: %6.4f\n", "10%", result[2]);
  }
}
