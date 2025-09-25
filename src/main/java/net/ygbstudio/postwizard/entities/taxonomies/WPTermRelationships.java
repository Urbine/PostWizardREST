package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.StringJoiner;
import net.ygbstudio.postwizard.entities.WPost;

/**
 * Represents a WordPress term relationship entity.
 *
 * <p>This class maps to the `wp_term_relationships` table and provides fields for various
 * attributes of a term relationship, such as object ID, term taxonomy ID, and term order.
 *
 * @see <a
 *     href="https://developer.wordpress.org/reference/classes/wp_term_relationships/">WP_Term_Relationships</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_term_relationships`")
public class WPTermRelationships {

  @EmbeddedId private WPTermRelationshipsID id;

  @ManyToOne(optional = false)
  @MapsId(value = "objectID")
  @JoinColumn(name = "object_id")
  private WPost postObject;

  @Column(name = "term_order")
  private int termOrder;

  @ManyToOne(optional = false)
  @MapsId(value = "termTaxonomyID")
  @JoinColumn(name = "term_taxonomy_id")
  private WPTerms wpTerm;

  public WPTermRelationships() {}

  public WPTermRelationships(
      WPTermRelationshipsID id, WPost postObject, int termOrder, WPTerms wpTerm) {
    this.id = id;
    this.postObject = postObject;
    this.termOrder = termOrder;
    this.wpTerm = wpTerm;
  }

  public WPTermRelationshipsID getId() {
    return id;
  }

  public void setId(WPTermRelationshipsID id) {
    this.id = id;
  }

  public WPost getPostObject() {
    return postObject;
  }

  public void setPostObject(WPost postObject) {
    this.postObject = postObject;
  }

  public int getTermOrder() {
    return termOrder;
  }

  public void setTermOrder(int termOrder) {
    this.termOrder = termOrder;
  }

  public WPTerms getWpTerm() {
    return wpTerm;
  }

  public void setWpTerm(WPTerms wpTerm) {
    this.wpTerm = wpTerm;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPTermRelationships.class.getSimpleName() + "[", "]")
        .add("objectID=" + id.getObjectID())
        .add("termTaxonomyId=" + id.getTermTaxonomyID())
        .add("postObject=" + postObject.getId())
        .add("termOrder=" + termOrder)
        .add("WPTerm=" + wpTerm.getName())
        .add("WPTermId=" + wpTerm.getId())
        .add("WPTermSlug=" + wpTerm.getSlug())
        .toString();
  }
}
