package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of production types for content classification purposes.
 *
 * @see net.ygbstudio.postwizard.services.PostMetaService
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum Production {
  PROFESSIONAL("Professional"),
  HOMEMADE("Homemade");

  private final String value;

  Production(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
