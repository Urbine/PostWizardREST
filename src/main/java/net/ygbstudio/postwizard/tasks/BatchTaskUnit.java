package net.ygbstudio.postwizard.tasks;

import jakarta.enterprise.concurrent.ManagedTask;
import jakarta.enterprise.concurrent.ManagedTaskListener;
import java.util.Map;
import net.ygbstudio.postwizard.tasks.listeners.BatchTaskListener;
import org.jspecify.annotations.NullMarked;

/**
 * BatchTaskUnit is a record that implements {@link ManagedTask} and {@link Runnable} interfaces. It
 * is used to represent a batch task unit in a batch processing operation usually carried out by the
 * service layer and submitted to a {@link jakarta.enterprise.concurrent.ManagedExecutorService} in
 * the controller layer.
 *
 * <p>The record contains the {@code postId}, {@code batchNumber}, {@code batchSize}, and a {@link
 * Runnable} {@code task}. It also implements the {@link ManagedTaskListener} interface to provide a
 * listener for logging.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public record BatchTaskUnit(long postId, int batchNumber, int batchSize, Runnable task)
    implements ManagedTask, Runnable {

  private String taskIdentity() {
    return "BatchTaskUnit #" + batchNumber + " out of " + batchSize + " -> Post #" + postId;
  }

  @Override
  public ManagedTaskListener getManagedTaskListener() {
    return BatchTaskListener.getInstance();
  }

  @Override
  public Map<String, String> getExecutionProperties() {
    return Map.of(LONGRUNNING_HINT, "false", IDENTITY_NAME, taskIdentity());
  }

  @Override
  public void run() {
    task.run();
  }
}
