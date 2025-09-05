package net.ygbstudio.postwizard.utils;

import org.jspecify.annotations.NullMarked;

/**
 * Utility class for debugging purposes. This class provides methods to assist in debugging by
 * retrieving information primarily located in stacktraces.
 *
 * <p>This class is designed to be a utility class, so it should not be instantiated.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public class Debugging implements Util {
  private Debugging() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Retrieves the name of the method that called this method.
   *
   * @param includeClass | If true, includes the class name in the returned string; otherwise,
   *     returns only the method name.
   * @return The name of the calling method, optionally prefixed by the class name.
   */
  public static String[] getCallingMethod(boolean includeClass) {
    StackTraceElement stack = Thread.currentThread().getStackTrace()[3];
    String className = stack.getClassName();
    String methodName = stack.getMethodName();
    return includeClass ? new String[] {className, methodName} : new String[] {methodName};
  }
}
