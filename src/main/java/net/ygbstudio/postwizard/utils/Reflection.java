/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.utils;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jspecify.annotations.NullMarked;

/**
 * Utility class for reflection-related operations.
 *
 * <p>Note: This class is not meant to be instantiated.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public final class Reflection implements Util {
  private Reflection() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Retrieves the values of the JsonbProperty annotations from the fields of the specified class
   * via reflection, excluding any fields that are marked with JsonbTransient.
   *
   * @param annotatedClass The class to inspect for JsonbProperty annotations.
   * @return A list of strings representing the values of the JsonbProperty annotations.
   */
  public static List<String> getJsonBPropertyValues(Class<?> annotatedClass) {

    return Stream.of(annotatedClass.getDeclaredFields())
        .filter(
            field ->
                !field.isAnnotationPresent(JsonbTransient.class)
                    && field.isAnnotationPresent(JsonbProperty.class))
        .map(field -> field.getAnnotation(JsonbProperty.class).value())
        .toList();
  }

  /**
   * Retrieves a stream of fields from the specified class, applying a mapping function to each
   * field. This method allows for flexible transformations of the fields, such as extracting field
   * names, types, or any other property of the field.
   *
   * @param declaringClass The class whose fields are to be retrieved.
   * @param transformer A function to apply to each field.
   * @return A stream of mapped fields.
   */
  public static <R> Stream<? extends R> getTransformClassFields(
      Class<?> declaringClass, Function<? super Field, ? extends R> transformer) {

    return Arrays.stream(declaringClass.getDeclaredFields()).map(transformer);
  }
}
