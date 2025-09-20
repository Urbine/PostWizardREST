package net.ygbstudio.postwizard.exceptions;

/**
 * Custom exception to indicate that a record identifier is invalid. This exception is thrown when
 * an operation attempts to access or manipulate a record with an identifier that does not exist or
 * is malformed. *
 *
 * <p>It extends {@link RuntimeException} to allow for unchecked exceptions and can be used in
 * various parts of the application to signal issues with record identifiers.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public class InvalidIdentifier extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidIdentifier() {
    super();
  }

  public InvalidIdentifier(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public InvalidIdentifier(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidIdentifier(String message) {
    super(message);
  }

  public InvalidIdentifier(Throwable cause) {
    super(cause);
  }
}
