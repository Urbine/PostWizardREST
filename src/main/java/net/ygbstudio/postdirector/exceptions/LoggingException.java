package net.ygbstudio.postdirector.exceptions;

/**
 * Custom exception class for logging checked exceptions in the PostDirector application.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class LoggingException extends Exception {

  public LoggingException() {
    super();
  }

  public LoggingException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public LoggingException(String message, Throwable cause) {
    super(message, cause);
  }

  public LoggingException(String message) {
    super(message);
  }

  public LoggingException(Throwable cause) {
    super(cause);
  }
}
