/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright (c) Yoham Gabriel B.
 */

package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a WordPress term taxonomy entity. Taxonomies in PostWizard are treated as absolute
 * parents of terms and their associated data. In order to create a new term, you must first create
 * a new taxonomy and then associate the new term with it, so that metadata can be added to the term
 * and post relationships can be established.
 *
 * <p>This class maps to the {@code wp_term_taxonomy} table and provides fields for various
 * attributes of a term taxonomy, such as term ID, taxonomy, description, parent, and count.
 *
 * @see WPTerms
 * @see WPTermMeta
 * @see WPTermRelationships
 * @see <a
 *     href="https://developer.wordpress.org/reference/classes/wp_term_taxonomy/">WP_Term_Taxonomy</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_term_taxonomy`")
@NamedQueries(
    value = {
      @NamedQuery(
          name = "WPTermTaxonomy.ExistsById",
          query = "SELECT t FROM WPTermTaxonomy t WHERE t.termTaxonomyId = :termTaxonomyId"),
      @NamedQuery(
          name = "WPTermTaxonomy.ExistsByTerm",
          query = "SELECT t FROM WPTermTaxonomy t WHERE t.term = :term"),
      @NamedQuery(
          name = "WPTermTaxonomy.ExistsByTaxonomy",
          query = "SELECT t FROM WPTermTaxonomy t WHERE t.taxonomy = :taxonomy")
    })
public class WPTermTaxonomy {
  @Id
  @Column(name = "term_taxonomy_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long termTaxonomyId;

  @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "term_id")
  private WPTerms term;

  @Column(name = "taxonomy")
  private String taxonomy;

  @Column(name = "description")
  private String description;

  @Column(name = "parent")
  private int parent;

  @Column(name = "count")
  private Long count;

  public Long getTermTaxonomyId() {
    return termTaxonomyId;
  }

  public void setTermTaxonomyId(Long termTaxonomyId) {
    this.termTaxonomyId = termTaxonomyId;
  }

  public WPTerms getTerm() {
    return term;
  }

  public void setTerm(WPTerms term) {
    this.term = term;
  }

  public String getTaxonomy() {
    return taxonomy;
  }

  public void setTaxonomy(String taxonomy) {
    this.taxonomy = taxonomy;
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
    WPTermTaxonomy that = (WPTermTaxonomy) o;
    return getParent() == that.getParent()
        && Objects.equals(getTermTaxonomyId(), that.getTermTaxonomyId())
        && Objects.equals(getTerm(), that.getTerm())
        && Objects.equals(getTaxonomy(), that.getTaxonomy())
        && Objects.equals(getDescription(), that.getDescription())
        && Objects.equals(getCount(), that.getCount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getTermTaxonomyId(), getTerm(), getTaxonomy(), getDescription(), getParent(), getCount());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPTermTaxonomy.class.getSimpleName() + "[", "]")
        .add("termTaxonomyId=" + termTaxonomyId)
        .add("termId=" + term)
        .add("taxonomy='" + taxonomy + "'")
        .add("description='" + description + "'")
        .add("parent=" + parent)
        .add("count=" + count)
        .toString();
  }
}
