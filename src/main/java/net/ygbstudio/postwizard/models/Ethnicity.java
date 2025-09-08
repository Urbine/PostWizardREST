package net.ygbstudio.postwizard.models;

public enum Ethnicity {
  Mixed("Mixed"),
  MiddleEastern("Middle Eastern"),
  Ebony("Ebony"),
  Asian("Asian"),
  Latino("Latino"),
  White("White"),
  Indian("Indian");

  private final String ethnicity;

  Ethnicity(String ethnicity) {
    this.ethnicity = ethnicity;
  }

  @Override
  public String toString() {
    return this.ethnicity;
  }
}
