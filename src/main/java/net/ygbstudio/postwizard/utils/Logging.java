package net.ygbstudio.postwizard.utils;

import static net.ygbstudio.postwizard.utils.Debugging.getCallingMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for logging functionalities in the PostWizard application. This class provides
 * methods to create and configure FileHandlers for logging, as well as to determine the logging
 * level based on environment variables.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public class Logging implements Util {

  private Logging() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Creates a FileHandler for logging to a specified file in the user's home directory under a
   * "PDLogs" folder.
   *
   * @param fileName The name of the log file.
   * @param loggingLevel The logging level for the FileHandler.
   * @param append Whether to append to the existing log file or overwrite it.
   * @return A FileHandler configured with the specified parameters.
   * @throws SecurityException if a security manager exists and it denies access to the file.
   * @throws IOException if an I/O error occurs while creating the FileHandler.
   */
  public static FileHandler logFileHandlerFactory(
      String fileName, Level loggingLevel, boolean append) throws SecurityException, IOException {
    String userDir = System.getProperty("user.dir");
    Path loggingPath = Path.of(userDir, "PDLogs");
    File logDir = new File(loggingPath.toString());
    if (!logDir.exists()) logDir.mkdir();

    FileHandler globalFileHandler = new FileHandler("%h" + "/PDLogs/" + fileName + "-%g", append);
    globalFileHandler.setLevel(loggingLevel);
    return globalFileHandler;
  }

  /**
   * Initialises a FileHandler for logging with the specified logger, logging level, and append
   * mode.
   *
   * @param classLogger The logger to which the FileHandler will be added.
   * @param handlerLevel The logging level for the FileHandler.
   * @param append Whether to append to the existing log file or overwrite it.
   * @return A FileHandler configured with the specified parameters, or null if an error occurs.
   */
  public static @Nullable FileHandler loggingInit(
      Logger classLogger, Level handlerLevel, boolean append) {
    try {
      FileHandler classFileHandler =
          logFileHandlerFactory(classLogger.getName(), handlerLevel, append);
      classFileHandler.setFormatter(new SimpleFormatter());
      classLogger.addHandler(classFileHandler);
      classLogger.setLevel(Logging.pickEnvLoggingLevel());
      return classFileHandler;
    } catch (SecurityException | IOException e) {
      Exceptions.unchecked(e);
    }
    return null;
  }

  /**
   * Picks the logging debug level based on the environment variable "PDLOG_LEVEL".
   *
   * <p>As logging becomes more advanced in this application, this method will accept more values.
   *
   * @return The logging level set by the environment variable, defaulting to INFO if not set or if
   *     the value is not recognized.
   */
  public static @NonNull Level pickEnvLoggingLevel() {
    String globalLogLevel = System.getenv("PWLOG_LEVEL");
    return Objects.requireNonNullElse(globalLogLevel, "").equalsIgnoreCase("DEBUG")
        ? Level.ALL
        : Level.INFO;
  }

  public static void logStepIn(@NonNull Logger classLogger, @NonNull Object... inputParams) {
    String[] stackInfo = getCallingMethod(true);
    classLogger.entering(stackInfo[0], stackInfo[1], inputParams);
  }

  public static void logStepOut(@NonNull Logger classLogger, @NonNull Object... inputParams) {
    String[] stackInfo = getCallingMethod(true);
    classLogger.exiting(stackInfo[0], stackInfo[1], inputParams);
  }
}
