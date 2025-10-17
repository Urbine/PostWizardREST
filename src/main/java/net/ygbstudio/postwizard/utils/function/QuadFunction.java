package net.ygbstudio.postwizard.utils.function;

/**
 * A functional interface similar to {@link java.util.function.Function}.
 *
 * <p>In this case, {@link QuadFunction} represents a function of four input parameters and a return
 * value of type {@code R}.
 *
 * @param <T> the type of the first input parameter
 * @param <U> the type of the second input parameter
 * @param <V> the type of the third input parameter
 * @param <W> the type of the fourth input parameter
 */
@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {

  /**
   * Applies the function to the given arguments just like the functional interface in the <br>
   * {@link java.util.function.Function} package.
   *
   * @see java.util.function.Function#apply(Object)
   * @param t the first input parameter
   * @param u the second input parameter
   * @param v the third input parameter
   * @param w the fourth input parameter
   * @return the result of the function
   */
  R apply(T t, U u, V v, W w);
}
