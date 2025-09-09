package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of production types for content classification purposes.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum Production {
  Professional("Professional"),
  Homemade("Homemade");

  private final String value;

  Production(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
