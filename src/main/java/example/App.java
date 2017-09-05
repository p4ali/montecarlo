package example;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
  private static final double INFLATION = 0.035;
  private static final int N = 100000; // total trials
  private static final int YEARS = 20; // total years

  public static void main(String[] args) {

    MonteCarlo mc = new MonteCarlo();
    double[] aggressive = mc.simulate(YEARS, N, 0.094324, 0.15675, INFLATION);
    mc.print(aggressive, "aggressive");

    double[] conservative = mc.simulate(YEARS, N, 0.06189, 0.063438, INFLATION);
    mc.print(conservative, "conservative");
  }
}
