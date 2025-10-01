package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
  @JoinColumn(name = "object_id", nullable = false)
  private WPost postObject;

  @OneToOne(optional = false)
  @MapsId(value = "termTaxonomyID")
  @JoinColumn(name = "term_taxonomy_id", nullable = false)
  private WPTermTaxonomy termTaxonomy;

  @Column(name = "term_order")
  private int termOrder;

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

  public WPTermTaxonomy getTermTaxonomy() {
    return termTaxonomy;
  }

  public void setTermTaxonomy(WPTermTaxonomy termTaxonomy) {
    this.termTaxonomy = termTaxonomy;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPTermRelationships.class.getSimpleName() + "[", "]")
        .add("objectID=" + id.getObjectID())
        .add("termTaxonomyId=" + id.getTermTaxonomyID())
        .add("postObject=" + postObject.getId())
        .add("termOrder=" + termOrder)
        .add("WPTermTaxonomy=" + termTaxonomy.getTaxonomy())
        .add("WPTermTaxonomyCount=" + termTaxonomy.getCount())
        .add("WPTermSlug=" + termTaxonomy.getDescription())
        .toString();
  }
}
