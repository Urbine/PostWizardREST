package net.ygbstudio.postwizard.models;

public enum Orientation {
  Straight("Straight"),
  Trans("Trans");

  private final String orientation;

  Orientation(String orientation) {
    this.orientation = orientation;
  }

  @Override
  public String toString() {
    return this.orientation;
  }
}
