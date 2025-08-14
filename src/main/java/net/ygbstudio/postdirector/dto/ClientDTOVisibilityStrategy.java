package net.ygbstudio.postdirector.dto;

import jakarta.json.bind.config.PropertyVisibilityStrategy;

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
  public boolean isVisible(java.lang.reflect.Field field) {
    // Make all fields visible since the application will be receiving schema from the client.
    return true;
  }

  @Override
  public boolean isVisible(java.lang.reflect.Method method) {
    // Ignores setters and getters to keep those as idiomatic as possible.
    return false;
  }
}
