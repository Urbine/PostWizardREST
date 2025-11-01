package net.ygbstudio.postwizard.tasks;

import static net.ygbstudio.postwizard.utils.Logging.logStepOut;
import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.exceptions.ScheduledTaskException;
import net.ygbstudio.postwizard.services.PostMetaService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class is a scheduled task that randomises the featured videos in the database. It is also
 * used as a wrapper for the randomiseFeaturedVideos method in the PostMetaService since
 * RandomiseFeaturedTask uses concurrency features that are controlled by the EJB container, such as
 * write locks.
 *
 * <p>The reason for this is that the randomiseFeaturedVideos method is called from a CDI REST
 * endpoint and accidental overlap of the scheduled task with the REST endpoint call could lead to
 * data loss, corruption, or other unexpected behaviour in concurrency management, such as deadlocks
 * or race conditions while dealing with mutable state in the PostMetaService layer.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Startup
@Singleton
public class RandomiseFeaturedTask {

  private final Logger randomiseFeaturedTaskLog =
      Logger.getLogger(RandomiseFeaturedTask.class.getName());

  @SuppressWarnings("unused")
  @Nullable
  private final FileHandler randomiseFeaturedWorkerFileHandler =
      loggingInit(randomiseFeaturedTaskLog, Level.ALL, true);

  @SuppressWarnings("unused")
  @Inject
  private PostMetaService postMetaService;

  @PostConstruct
  public void init() {
    randomiseFeaturedTaskLog.log(
        Level.INFO,
        () ->
            "RandomiseFeaturedTask started and it is ready to run. Timestamp: "
                + Instant.now().toString());
  }

  @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
  @Lock(LockType.WRITE)
  public void randomiseAtMidnight() {
    try {
      int numberOfFeaturedVideos = 20;
      Set<Long> newFeaturedVideos = postMetaService.randomiseFeaturedVideos(numberOfFeaturedVideos);
      if (newFeaturedVideos.isEmpty())
        throw new ScheduledTaskException(
            "RandomisedFeaturedWorker failed to get a new batch of featured videos");
      randomiseFeaturedTaskLog.log(
          Level.INFO, () -> "RandomiseFeaturedTask completed at " + Instant.now().toString());

    } catch (Exception anyEx) {
      /*
       In theory, method randomiseFeaturedVideos() should return a list with new randomised featured videos
       every time it is called, but in case it fails, we throw a ScheduledTaskException.
       These catch blocks are more of a placeholder for any exception that may be thrown
       by the PostMetaService or the database driver, specially exceptions that have to do with
       connection to the database, connection pool exhaustion or timeouts that usually happen in
       the Data Access layer. These catch blocks are broad on purpose, loggers are in place in both.
      */
      randomiseFeaturedTaskLog.log(
          Level.SEVERE, "randomiseAtMidnight scheduled task failed", anyEx);
      logStepOut(
          randomiseFeaturedTaskLog,
          "RandomiseFeaturedWorker failed -> " + anyEx.getMessage(),
          anyEx,
          anyEx.getCause());
    }
  }

  @Lock(LockType.WRITE)
  @NonNull
  public Set<Long> randomiseFeaturedVideosBean(int limit) {
    try {
      return postMetaService.randomiseFeaturedVideos(limit);
    } catch (Exception anyEx) {
      randomiseFeaturedTaskLog.log(Level.SEVERE, "randomiseFeaturedVideosBean failed", anyEx);
      logStepOut(
          randomiseFeaturedTaskLog,
          "randomiseFeaturedVideosBean failed -> " + anyEx.getMessage(),
          anyEx,
          anyEx.getCause());
    }
    return Collections.emptySet();
  }
}
