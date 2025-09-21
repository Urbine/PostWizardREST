package net.ygbstudio.postwizard.exceptions;

/**
 * Custom exception to indicate that a scheduled task has failed.
 *
 * <p>It extends {@link RuntimeException} to allow for unchecked exceptions and can be used in
 * various parts of the application to signal issues with scheduled tasks.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public class ScheduledTaskException extends RuntimeException {
  public ScheduledTaskException(String message) {
    super(message);
  }
}
