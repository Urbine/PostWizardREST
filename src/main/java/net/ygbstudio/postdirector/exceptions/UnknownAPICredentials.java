package net.ygbstudio.postdirector.exceptions;

/**
 * Exception thrown when API credentials are unknown or invalid. This exception is used to indicate
 * that the provided API credentials do not match any known user or are not recognized by the
 * system.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class UnknownAPICredentials extends Exception {

  public UnknownAPICredentials() {
    super();
    // TODO Auto-generated constructor stub
  }

  public UnknownAPICredentials(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

  public UnknownAPICredentials(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public UnknownAPICredentials(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public UnknownAPICredentials(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
}
