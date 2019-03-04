package tech.simter.start.r2dbc;

/**
 * @author RJ
 */
public class TestUtils {
  private static int id = 100;

  /**
   * Generate a new id.
   */
  public static Integer nextId() {
    return ++id;
  }
}
