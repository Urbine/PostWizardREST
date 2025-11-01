package net.ygbstudio.postwizard.tasks.listeners;

import static net.ygbstudio.postwizard.utils.Logging.loggingInit;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.concurrent.ManagedTask;
import jakarta.enterprise.concurrent.ManagedTaskListener;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ygbstudio.postwizard.tasks.BatchTaskUnit;
import org.jspecify.annotations.Nullable;

/**
 * BatchTaskListener implements the {@link ManagedTaskListener} interface.
 *
 * <p>It is used to listen to and log lifecycle events of a {@link BatchTaskUnit} in a batch
 * processing operation.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class BatchTaskListener implements ManagedTaskListener {

  private static final Logger batchTaskListenerLog =
      Logger.getLogger(BatchTaskListener.class.getSimpleName());

  @SuppressWarnings("unused")
  @Nullable
  private static final FileHandler batchTaskListenerFileHandler =
      loggingInit(batchTaskListenerLog, Level.ALL, true);

  private static final BatchTaskListener singleInstance = new BatchTaskListener();

  private BatchTaskListener() {}

  public static BatchTaskListener getInstance() {
    return BatchTaskListener.singleInstance;
  }

  @Override
  public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
    BatchTaskUnit batchTaskUnit = (BatchTaskUnit) task;
    batchTaskListenerLog.info(
        () ->
            "Task submitted: "
                + batchTaskUnit.getExecutionProperties().get(ManagedTask.IDENTITY_NAME));
  }

  @Override
  public void taskAborted(
      Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
    BatchTaskUnit batchTaskUnit = (BatchTaskUnit) task;
    batchTaskListenerLog.info(
        () ->
            "Task aborted: "
                + batchTaskUnit.getExecutionProperties().get(ManagedTask.IDENTITY_NAME)
                + "due to "
                + exception.getMessage()
                + " caused by"
                + exception.getCause()
                + ". StackTrace: "
                + Arrays.toString(exception.getStackTrace()));
  }

  @Override
  public void taskDone(
      Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
    BatchTaskUnit batchTaskUnit = (BatchTaskUnit) task;
    batchTaskListenerLog.info(
        () ->
            "Task Done: " + batchTaskUnit.getExecutionProperties().get(ManagedTask.IDENTITY_NAME));
  }

  @Override
  public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
    BatchTaskUnit batchTaskUnit = (BatchTaskUnit) task;
    batchTaskListenerLog.info(
        () ->
            "Task about to start: "
                + batchTaskUnit.getExecutionProperties().get(ManagedTask.IDENTITY_NAME));
  }
}
