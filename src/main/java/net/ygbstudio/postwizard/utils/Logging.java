package net.ygbstudio.postwizard.utils;

import static net.ygbstudio.postwizard.utils.Debugging.getCallingMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
   * Creates a FileHandler for logging to a specified file under a "PWLogs" folder. in the local
   * Java classpath.
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

    String platformSeparator = System.getProperty("file.separator");
    Path logsPath = Paths.get(".", "PWLogs");
    File logDir = new File(logsPath.toString());
    if (!logDir.exists()) logDir.mkdir();

    FileHandler globalFileHandler =
        new FileHandler(
            logsPath.toAbsolutePath().toString() + platformSeparator + fileName + "-%g", append);
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

  /**
   * Logs the entry into a method along with its input parameters. The method automatically
   * determines the calling class and method name.
   *
   * @param classLogger The logger to use for logging.
   * @param inputParams The input parameters of the method being logged.
   */
  public static void logStepIn(@NonNull Logger classLogger, @NonNull Object... inputParams) {
    String[] stackInfo = getCallingMethod(true);
    classLogger.entering(stackInfo[0], stackInfo[1], inputParams);
  }

  /**
   * Logs the exit from a method along with its output parameters. The method automatically
   * determines the calling class and method name.
   *
   * @param classLogger The logger to use for logging.
   * @param inputParams The output parameters of the method being logged.
   */
  public static void logStepOut(@NonNull Logger classLogger, @NonNull Object... outputParams) {
    String[] stackInfo = getCallingMethod(true);
    classLogger.exiting(stackInfo[0], stackInfo[1], outputParams);
  }

  /**
   * Logs the path reached in a controller along with the IP address of the requester. Particularly
   * useful for RESTful services to track endpoint access.
   *
   * @param classLogger The logger to use for logging.
   * @param context The UriInfo context containing path information.
   * @param request The HttpServletRequest containing requester information.
   */
  public static void logControllerPath(
      Logger classLogger, UriInfo context, HttpServletRequest request) {
    classLogger.fine("Reached path: " + context.getPath() + "from IP" + request.getRemoteAddr());
  }
}
