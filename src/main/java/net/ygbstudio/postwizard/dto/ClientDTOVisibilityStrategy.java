/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A custom field access strategy for JSON-B that makes all fields visible to the client, while
 * ignoring setters and getters.
 *
 * <p>This is useful when the application needs to receive a schema from the client, allowing all
 * fields to be serialized and deserialized without restrictions.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class ClientDTOVisibilityStrategy implements PropertyVisibilityStrategy {

  @Override
  public boolean isVisible(Field field) {
    // Make all fields visible since the application will be receiving schema from the client.
    return true;
  }

  @Override
  public boolean isVisible(Method method) {
    // Ignores setters and getters to keep those as idiomatic as possible.
    return false;
  }
}
