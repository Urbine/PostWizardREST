package net.ygbstudio.postwizard.entities.taxonomies;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents metadata associated with a WordPress term.
 *
 * <p>This entity maps to the {@code wp_termmeta} table and allows reading and updating key-value
 * metadata pairs for terms.
 *
 * @see <a href="https://developer.wordpress.org/reference/classes/wp_termmeta/">WP_Term_Meta</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_termmeta`")
public class WPTermMeta {
  @Id
  @Column(name = "meta_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long metaId;

  @Column(name = "meta_key")
  private String metaKey;

  @Column(name = "meta_value")
  private String metaValue;

  @ManyToOne(optional = false)
  @JoinColumn(name = "term_id")
  private WPTerms termItem;

  private WPTermMeta() {}

  public WPTermMeta(Long metaId, String metaKey, String metaValue, WPTerms termItem) {
    this.metaId = metaId;
    this.metaKey = metaKey;
    this.metaValue = metaValue;
    this.termItem = termItem;
  }

  public Long getMetaId() {
    return metaId;
  }

  public void setMetaId(Long metaId) {
    this.metaId = metaId;
  }

  public String getMetaKey() {
    return metaKey;
  }

  public void setMetaKey(String metaKey) {
    this.metaKey = metaKey;
  }

  public String getMetaValue() {
    return metaValue;
  }

  public void setMetaValue(String metaValue) {
    this.metaValue = metaValue;
  }

  public WPTerms getTermItem() {
    return termItem;
  }

  public void setTermItem(WPTerms termItem) {
    this.termItem = termItem;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    WPTermMeta that = (WPTermMeta) o;
    return Objects.equals(getMetaId(), that.getMetaId())
        && Objects.equals(getMetaKey(), that.getMetaKey())
        && Objects.equals(getMetaValue(), that.getMetaValue())
        && Objects.equals(getTermItem(), that.getTermItem());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMetaId(), getMetaKey(), getMetaValue(), getTermItem());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", WPTermMeta.class.getSimpleName() + "[", "]")
        .add("metaId=" + metaId)
        .add("metaKey='" + metaKey + "'")
        .add("metaValue='" + metaValue + "'")
        .add("termId=" + termItem.getId())
        .add("wpTermName=" + termItem.getName())
        .add("wpTermSlug=" + termItem.getSlug())
        .add("wpTermId=" + termItem.getId())
        .toString();
  }
}
