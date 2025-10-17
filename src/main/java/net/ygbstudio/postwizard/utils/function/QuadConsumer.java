package net.ygbstudio.postwizard.utils.function;

/**
 * A functional interface similar to {@link java.util.function.Consumer}. <br>
 * In this case, {@link QuadConsumer} represents a consumer of four input parameters and no return
 * value.
 *
 * @param <T> the type of the first input parameter
 * @param <U> the type of the second input parameter
 * @param <V> the type of the third input parameter
 * @param <W> the type of the fourth input parameter
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {

  /**
   * Performs the operation on the given arguments just like the functional interface in the {@link
   * java.util.function.Consumer} package.
   *
   * @see java.util.function.Consumer#accept(Object)
   * @param t the first input parameter
   * @param u the second input parameter
   * @param v the third input parameter
   * @param w the fourth input parameter
   */
  void accept(T t, U u, V v, W w);
}
