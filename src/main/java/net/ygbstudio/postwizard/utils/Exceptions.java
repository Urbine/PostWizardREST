package net.ygbstudio.postwizard.utils;

import org.jspecify.annotations.NullMarked;

/**
 * Utility class for handling exceptions in the postwizard application. Provides methods to convert
 * checked exceptions to unchecked exceptions and to throw exceptions without declaring them.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public class Exceptions implements Util {

  private Exceptions() {
    throw new AssertionError("Unable to instantiate utility class");
  }

  /**
   * Converts a checked exception to an unchecked exception.
   *
   * @param e the checked exception to convert
   * @return a RuntimeException wrapping the original exception
   */
  public static RuntimeException unchecked(Exception e) {
    return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
  }

  /**
   * Throws an exception without declaring it.
   *
   * @param e the exception to throw
   * @param <T> the type of the value to return (not used)
   * @return never returns, always throws the exception
   */
  public static <T> T sneakyThrow(Exception e) {
    return Exceptions.sneakyThrowInternal(e);
  }

  @SuppressWarnings("unchecked")
  private static <E extends Throwable, T> T sneakyThrowInternal(Throwable t) throws E {
    throw (E) t;
  }
}
