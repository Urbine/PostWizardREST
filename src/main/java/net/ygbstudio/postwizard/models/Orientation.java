package net.ygbstudio.postwizard.models;

/**
 * Enum class representing a series of sexual orientations for content classification purposes.
 *
 * @author Yoham Gabriel B @ YGB Studio
 */
public enum Orientation {
  Straight("Straight"),
  Trans("Trans");

  private final String orientation;

  Orientation(String orientation) {
    this.orientation = orientation;
  }

  @Override
  public String toString() {
    return orientation;
  }
}
