package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Represents a WordPress term entity.
 *
 * <p>This class maps to the `wp_terms` table and provides fields for various attributes of a term,
 * such as name, slug, and term group.
 *
 * @see <a href="https://developer.wordpress.org/reference/classes/wp_term/">WP_Term</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_terms`")
@NamedQuery(name = "WPTerms.FindAll", query = "SELECT t FROM WPTerms t")
@NamedQuery(name = "WPTerms.ExistsByID", query = "SELECT t FROM WPTerms t WHERE t.id = :termId")
@NamedQuery(
    name = "WPTerms.ExistsBySlug",
    query = "SELECT t FROM WPTerms t WHERE t.slug = :termSlug")
@NamedQuery(
    name = "WPTerms.ExistsByName",
    query = "SELECT t FROM WPTerms t WHERE t.name = :termName")
public class WPTerms {
  @Id
  @Column(name = "term_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "termItem", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<WPTermMeta> termMeta;

  @OneToOne(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
  private WPTermTaxonomy taxonomy;

  @Column(name = "name")
  private String name;

  @Column(name = "slug")
  private String slug;

  @Column(name = "term_group")
  private long termGroup;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<WPTermMeta> getTermMeta() {
    return termMeta;
  }

  public void setTermMeta(Set<WPTermMeta> termMeta) {
    this.termMeta = termMeta;
  }

  public WPTermTaxonomy getTaxonomy() {
    return taxonomy;
  }

  public void setTaxonomy(WPTermTaxonomy taxonomy) {
    this.taxonomy = taxonomy;
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    WPTerms wpTerms = (WPTerms) o;
    return getTermGroup() == wpTerms.getTermGroup()
        && Objects.equals(getId(), wpTerms.getId())
        && Objects.equals(getName(), wpTerms.getName())
        && Objects.equals(getSlug(), wpTerms.getSlug());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getSlug(), getTermGroup());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPTerms.class.getSimpleName() + "[", "]")
        .add("termId=" + id)
        .add("name='" + name + "'")
        .add("slug='" + slug + "'")
        .add("termGroup=" + termGroup)
        .add(
            "termsMeta="
                + termMeta.stream().map(Objects::toString).collect(Collectors.joining(",")))
        .add("wpTaxonomy=" + taxonomy.getTaxonomy())
        .add("wpTaxonomyDescription" + taxonomy.getDescription())
        .add("wpTaxonomyCount" + taxonomy.getCount())
        .toString();
  }
}
