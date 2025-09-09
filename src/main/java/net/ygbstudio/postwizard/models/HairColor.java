package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of hair colors for content classification purposes.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum HairColor {
  Blonde("Blonde"),
  Brown("Brown"),
  Black("Black"),
  Red("Red"),
  Other("Other");

  private final String color;

  HairColor(String color) {
    this.color = color;
  }

  @Override
  public String toString() {
    return this.color;
  }
}
