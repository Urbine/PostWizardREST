package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of hair colors for content classification purposes.
 *
 * @see net.ygbstudio.postwizard.service.PostMetaService
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum HairColor {
  BLONDE("Blonde"),
  BROWN("Brown"),
  BLACK("Black"),
  RED("Red"),
  OTHER("Other");

  private final String color;

  HairColor(String color) {
    this.color = color;
  }

  @Override
  public String toString() {
    return color;
  }
}
