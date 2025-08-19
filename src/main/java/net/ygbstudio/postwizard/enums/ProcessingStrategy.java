package net.ygbstudio.postwizard.enums;

/**
 * Enum representing the processing strategies for tasks in the postwizard application.
 *
 * <p>This enum defines two strategies: - PARALLEL: Tasks are processed concurrently, allowing for
 * faster execution. - SEQUENTIAL: Tasks are processed one after another, ensuring order of
 * execution.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum ProcessingStrategy {
  PARALLEL,
  SEQUENTIAL,
}
