/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.StringJoiner;
import net.ygbstudio.postwizard.entities.WPost;

/**
 * Represents a WordPress term relationship entity. Term relationships are created separately from
 * the term taxonomy, term, and term metadata lifecycle, which means that a term can exist without
 * any relationships, but a relationship cannot exist without a term taxonomy.
 *
 * <p>As of now, term deletions do not cascade to their relationships, so they have to be deleted
 * manually as a cleanup process. Deletions involve a large number of posts that might be associated
 * with a term taxonomy, so it is recommended to delete the term taxonomy first and then delete the
 * term relationships.
 *
 * <p>The dedicated DAO interface provides a method for this called {@link
 * net.ygbstudio.postwizard.dao.TermRelationshipsManager#cleanTaxonomyRelationships(long)} and the
 * service layer uses it in conjunction with other methods in {@link
 * net.ygbstudio.postwizard.dao.TaxonomyDAO} to delete a term taxonomy cleanly.
 *
 * <p>This class maps to the {@code wp_term_relationships} table and provides fields for various
 * attributes of a term relationship, such as object ID, term taxonomy ID, and term order.
 *
 * @see <a
 *     href="https://developer.wordpress.org/reference/classes/wp_term_relationships/">WP_Term_Relationships</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_term_relationships`")
@NamedQueries(
    value = {
      @NamedQuery(
          name = "WPTermRelationships.FindExisting",
          query =
              "SELECT rel FROM WPTermRelationships rel WHERE rel.id.objectID = :objectID AND rel.id.termTaxonomyID = :termTaxonomyID"),
      @NamedQuery(
          name = "WPTermRelationships.DeleteByTermTaxonomyId",
          query =
              "DELETE FROM WPTermRelationships rel WHERE rel.id.termTaxonomyID = :termTaxonomyID"),
      @NamedQuery(
          name = "WPTermRelationships.CountByTermTaxonomyId",
          query =
              "SELECT COUNT(rel) FROM WPTermRelationships rel WHERE rel.id.termTaxonomyID = :termTaxonomyID")
    })
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
        .add("WPTermSlug=" + termTaxonomy.getTerm().getSlug())
        .add("WPTermName=" + termTaxonomy.getTerm().getName())
        .add("WPTermId=" + termTaxonomy.getTerm().getId())
        .toString();
  }
}
