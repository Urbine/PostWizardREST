package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of toggle fields that serve as a validation for boolean values
 * that have a different representation in the database.
 *
 * @see net.ygbstudio.postwizard.services.PostMetaService
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum ToggleField {
  ON("on"),
  OFF("off"),
  YES("yes"),
  NO("no");

  private final String value;

  ToggleField(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
