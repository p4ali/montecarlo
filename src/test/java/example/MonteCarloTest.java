package example;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Ignore;
import org.junit.Test;

public class MonteCarloTest {
  private static final double INFLATION = 0.035;
  private static final int N = 100000; // total trials
  private static final int YEARS = 20; // total years
  private static final double AGGRESSIVE_RETURN = 0.094324; // return mean
  private static final double AGGRESSIVE_RISK = 0.15675; // standard deviation
  private static final double CONSERVATIVE_RETURN = 0.06189; // return mean
  private static final double CONSERVATIVE_RISK = 0.063438; // standard deviation

  @Test
  @Ignore
  public void testSave() throws IOException {
    String basePath = System.getProperty("user.dir");
    MonteCarlo mc = new MonteCarlo();

    Path path = Paths.get(String.format("%s/output/%s.dat", basePath, "aggressive"));
    if (Files.exists(path)) Files.delete(path);
    assertFalse(Files.exists(path));
    double[] aggressive = mc.simulateAndSave(YEARS, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION, path);
    mc.print(aggressive, "aggressive");
    assertTrue(Files.exists(path));

    path = Paths.get(String.format("%s/output/%s.dat", basePath, "conservative"));
    if (Files.exists(path)) Files.delete(path);
    assertFalse(Files.exists(path));
    double[] conservative = mc.simulateAndSave(YEARS, N, CONSERVATIVE_RETURN, CONSERVATIVE_RISK, INFLATION, path);
    mc.print(conservative, "conservative");
    assertTrue(Files.exists(path));
  }

  @Test
  public void testSimulation() throws IOException {
    MonteCarlo mc = new MonteCarlo();
    double[] aggressive = mc.simulate(YEARS, N, AGGRESSIVE_RETURN, AGGRESSIVE_RISK, INFLATION);
    mc.print(aggressive, "aggressive");

    double[] conservative = mc.simulate(YEARS, N, CONSERVATIVE_RETURN, CONSERVATIVE_RISK, INFLATION);
    mc.print(conservative, "conservative");

    assertTrue("Aggressive average return should great than Conservative", aggressive[0] > conservative[0]);
    assertTrue("Aggressive %10 best case should great than Conservative", aggressive[1] > conservative[1]);
    assertTrue("Aggressive %10 worst case should less than Conservative", aggressive[2] < conservative[2]);

  }
}
