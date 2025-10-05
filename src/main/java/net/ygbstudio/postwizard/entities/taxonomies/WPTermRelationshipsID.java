package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Class {@code WPTermRelationshipsID} represents a composite of the primary keys of the {@code
 * wp_term_relationships} table. It is used as the embedded primary key of the {@link
 * WPTermRelationships} entity.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@Embeddable
public class WPTermRelationshipsID {

  @Column(name = "object_id")
  private Long objectID;

  @Column(name = "term_taxonomy_id")
  private Long termTaxonomyID;

  public Long getObjectID() {
    return objectID;
  }

  public void setObjectID(Long objectID) {
    this.objectID = objectID;
  }

  public Long getTermTaxonomyID() {
    return termTaxonomyID;
  }

  public void setTermTaxonomyID(Long termTaxonomyID) {
    this.termTaxonomyID = termTaxonomyID;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    WPTermRelationshipsID that = (WPTermRelationshipsID) o;
    return Objects.equals(getObjectID(), that.getObjectID())
        && Objects.equals(getTermTaxonomyID(), that.getTermTaxonomyID());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getObjectID(), getTermTaxonomyID());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPTermRelationshipsID.class.getSimpleName() + "[", "]")
        .add("objectID=" + objectID)
        .add("termTaxonomyID=" + termTaxonomyID)
        .toString();
  }
}
