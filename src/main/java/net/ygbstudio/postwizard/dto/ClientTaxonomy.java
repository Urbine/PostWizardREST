package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * DTO class that represents a taxonomy and is used for client operations concerning taxonomies
 * without the need to use the JPA-managed entity {@code WPTermTaxonomy}.
 *
 * @see net.ygbstudio.postwizard.entities.taxonomies.WPTermTaxonomy
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder({"termTaxonomyId", "term", "taxonomy", "description", "parent", "count"})
public class ClientTaxonomy implements EmbeddedResult {

  @JsonbNillable
  @JsonbProperty("term_taxonomy_id")
  private Long termTaxonomyId;

  @JsonbNillable
  @JsonbProperty("taxonomy_name")
  private String taxonomyName;

  @JsonbNillable
  @JsonbProperty("taxonomy_description")
  private String description;

  @JsonbNillable
  @JsonbProperty("taxonomy_parent")
  private int parent;

  @JsonbNillable
  @JsonbProperty("taxonomy_count")
  private Long count;

  public Long getTermTaxonomyId() {
    return termTaxonomyId;
  }

  public void setTermTaxonomyId(Long termTaxonomyId) {
    this.termTaxonomyId = termTaxonomyId;
  }

  public String getTaxonomyName() {
    return taxonomyName;
  }

  public void setTaxonomyName(String taxonomyName) {
    this.taxonomyName = taxonomyName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getParent() {
    return parent;
  }

  public void setParent(int parent) {
    this.parent = parent;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ClientTaxonomy that = (ClientTaxonomy) o;
    return getParent() == that.getParent()
        && Objects.equals(getTermTaxonomyId(), that.getTermTaxonomyId())
        && Objects.equals(getTaxonomyName(), that.getTaxonomyName())
        && Objects.equals(getDescription(), that.getDescription())
        && Objects.equals(getCount(), that.getCount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getTermTaxonomyId(), getTaxonomyName(), getDescription(), getParent(), getCount());
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, false);
  }
}
