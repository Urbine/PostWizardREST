package net.ygbstudio.postwizard.models;

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
