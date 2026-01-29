/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.models;

/**
 * Enum class that intends to model common URL paths within a WordPress site.
 *
 * <p>This class is useful to construct internal paths in a less error-prone manner.
 *
 * <p>This class is not exhaustive and more paths may be added in the future depending on the needs
 * of the application.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum InternalPath {
  WP_CONTENT("wp-content"),
  UPLOADS("uploads");

  private final String value;

  InternalPath(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
