package net.ygbstudio.postwizard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents WordPress options stored in the database.
 *
 * <p>WordPress options are key-value settings that control the behavior of the WordPress
 * installation. They are stored in the {@code wp_options} table and are used to store various
 * configuration settings, such as site URL, home URL, blog name, blog description, admin email, and
 * timezone string.
 *
 * <p>This entity may be referred in other parts of the codebase as "environment" because it
 * contains information used to make sense of the current site and how it is configured.
 *
 * @see <a href="https://developer.wordpress.org/reference/classes/wp_option/">WP_Option</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_options`")
@NamedQueries(
    value = {
      @NamedQuery(name = "WPOptions.FindAll", query = "SELECT o FROM WPOptions o"),
      @NamedQuery(
          name = "WPOptions.FindByOptionName",
          query = "SELECT o FROM WPOptions o WHERE o.optionName = :optionName")
    })
public class WPOptions {

  @Id
  @Column(name = "option_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long optionId;

  @Column(name = "option_name")
  private String optionName;

  @Column(name = "option_value")
  private String optionValue;

  @Column(name = "autoload")
  private String autoload;

  public long getOptionId() {
    return optionId;
  }

  public void setOptionId(long optionId) {
    this.optionId = optionId;
  }

  public String getOptionValue() {
    return optionValue;
  }

  public void setOptionValue(String optionValue) {
    this.optionValue = optionValue;
  }

  public String getOptionName() {
    return optionName;
  }

  public void setOptionName(String optionName) {
    this.optionName = optionName;
  }

  public String getAutoload() {
    return autoload;
  }

  public void setAutoload(String autoload) {
    this.autoload = autoload;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    WPOptions wpOptions = (WPOptions) o;
    return getOptionId() == wpOptions.getOptionId()
        && Objects.equals(getOptionName(), wpOptions.getOptionName())
        && Objects.equals(getOptionValue(), wpOptions.getOptionValue())
        && Objects.equals(getAutoload(), wpOptions.getAutoload());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOptionId(), getOptionName(), getOptionValue(), getAutoload());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPOptions.class.getSimpleName() + "[", "]")
        .add("optionId=" + optionId)
        .add("optionName='" + optionName + "'")
        .add("optionValue='" + optionValue + "'")
        .add("autoload='" + autoload + "'")
        .toString();
  }
}
