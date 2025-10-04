package net.ygbstudio.postwizard.dto;

import jakarta.json.bind.annotation.JsonbNillable;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * DTO class that represents a term and is used for client operations concerning terms without the
 * need to use the JPA-managed entity {@code WPTerms}. Not all fields are used in the client-side
 * operations, as some are not needed in most cases as the service layer handles validation and
 * conversion of relevant fields.
 *
 * @see net.ygbstudio.postwizard.entities.taxonomies.WPTerms
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonbPropertyOrder(value = {"termId", "name", "slug", "termGroup", "taxonomy"})
public class ClientTerm implements EmbeddedResult {

  @JsonbNillable
  @JsonbProperty("term_id")
  private Long termId;

  @JsonbNillable
  @JsonbProperty("term")
  private String name;

  @JsonbNillable
  @JsonbProperty("slug")
  private String slug;

  @JsonbNillable
  @JsonbProperty("term_group")
  private long termGroup;

  @JsonbNillable
  @JsonbProperty("taxonomy")
  private ClientTaxonomy taxonomy;

  public Long getTermId() {
    return termId;
  }

  public void setTermId(Long termId) {
    this.termId = termId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public long getTermGroup() {
    return termGroup;
  }

  public void setTermGroup(long termGroup) {
    this.termGroup = termGroup;
  }

  public ClientTaxonomy getTaxonomy() {
    return taxonomy;
  }

  public void setTaxonomy(ClientTaxonomy taxonomy) {
    this.taxonomy = taxonomy;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ClientTerm that = (ClientTerm) o;
    return getTermGroup() == that.getTermGroup()
        && Objects.equals(getTermId(), that.getTermId())
        && Objects.equals(getName(), that.getName())
        && Objects.equals(getSlug(), that.getSlug())
        && Objects.equals(getTaxonomy(), that.getTaxonomy());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTermId(), getName(), getSlug(), getTermGroup(), getTaxonomy());
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE, false);
  }
}
