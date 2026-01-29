/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of ethnicities for content classification purposes.
 *
 * @see net.ygbstudio.postwizard.services.PostMetaService
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum Ethnicity {
  MIXED("Mixed"),
  MIDDLE_EASTERN("Middle Eastern"),
  EBONY("Ebony"),
  ASIAN("Asian"),
  LATINO("Latino"),
  WHITE("White"),
  INDIAN("Indian");

  private final String value;

  Ethnicity(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
