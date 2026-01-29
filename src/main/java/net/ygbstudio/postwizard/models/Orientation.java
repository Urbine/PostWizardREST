/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of sexual orientations for content classification purposes.
 *
 * @see net.ygbstudio.postwizard.services.PostMetaService
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum Orientation {
  STRAIGHT("Straight"),
  TRANS("Trans");

  private final String orientation;

  Orientation(String orientation) {
    this.orientation = orientation;
  }

  @Override
  public String toString() {
    return orientation;
  }
}
