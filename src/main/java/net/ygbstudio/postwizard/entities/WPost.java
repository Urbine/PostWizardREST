package net.ygbstudio.postwizard.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.ygbstudio.postwizard.entities.taxonomies.WPTermRelationships;

/**
 * Represents a WordPress post entity. Posts are the primary entities in PostWizard and are used to
 * store content, such as pages, posts, and custom post types. They are also associated with terms
 * through term relationships, which are not part of the lifecycle of term taxonomies.
 *
 * <p>As of now, post deletions cascade to their relationships and metadata, but leave term
 * taxonomies untouched since a term can be related to multiple posts. This means that posts are
 * parent entities of {@link WPTermRelationships} and {@link WPMeta} entities.
 *
 * <p>This class maps to the {@code wp_posts} table and provides fields for various attributes of a
 * post, such as author, content, title, slug, status, and type.
 *
 * @see WPTermRelationships
 * @see WPMeta
 * @see <a href="https://developer.wordpress.org/reference/classes/wp_post/">WP_Post</a>
 * @author Yoham Gabriel @ YGB Studio
 */
@Entity
@Table(name = "`wp_posts`")
@NamedQuery(name = "WPost.FindAll", query = "SELECT p FROM WPost p")
@NamedQuery(name = "WPost.FindByID", query = "SELECT p from WPost p WHERE p.id = :postId")
@NamedQuery(name = "WPost.FindByType", query = "SELECT p FROM WPost p WHERE p.postType = :postType")
@NamedQuery(
    name = "WPost.FindMediaByTitle",
    query = "SELECT p FROM WPost p WHERE p.postType = 'attachment' AND p.postTitle = :title")
@NamedQuery(
    name = "WPost.FindMediaByTitleLike",
    query = "SELECT p FROM WPost p WHERE p.postType = 'attachment' AND p.postTitle LIKE :title")
public class WPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "post_author")
  private Long postAuthor;

  @Column(name = "post_date_gmt")
  private LocalDateTime createdAtGMT;

  @Column(name = "post_date")
  private LocalDateTime createdAtLocal;

  @Column(name = "post_modified_gmt")
  private LocalDateTime modifiedAtGMT;

  @Column(name = "post_modified")
  private LocalDateTime modifiedAtLocal;

  @Column(name = "post_content")
  private String postContent;

  @Column(name = "post_title")
  private String postTitle;

  @Column(name = "post_name")
  private String postSlug;

  @Column(name = "post_status")
  private String postStatus;

  @Column(name = "post_type")
  private String postType;

  @Column(name = "post_parent")
  private Long postParent;

  @Column(name = "guid")
  private String guid;

  @Column(name = "post_mime_type")
  private String postMimeType;

  @OneToMany(mappedBy = "postObject", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<WPTermRelationships> termRelationships;

  @OneToMany(mappedBy = "wpPost", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<WPMeta> postMetadataSet;

  public Long getId() {
    return id;
  }

  public void setId(Long iD) {
    id = iD;
  }

  public Long getPostAuthor() {
    return postAuthor;
  }

  public void setPostAuthor(Long postAuthor) {
    this.postAuthor = postAuthor;
  }

  public LocalDateTime getCreatedAtGMT() {
    return createdAtGMT;
  }

  public void setCreatedAtGMT(LocalDateTime createdAtGMT) {
    this.createdAtGMT = createdAtGMT;
  }

  public LocalDateTime getCreatedAtLocal() {
    return createdAtLocal;
  }

  public void setCreatedAtLocal(LocalDateTime createdAtLocal) {
    this.createdAtLocal = createdAtLocal;
  }

  public LocalDateTime getModifiedAtGMT() {
    return modifiedAtGMT;
  }

  public void setModifiedAtGMT(LocalDateTime modifiedAtGMT) {
    this.modifiedAtGMT = modifiedAtGMT;
  }

  public LocalDateTime getModifiedAtLocal() {
    return modifiedAtLocal;
  }

  public void setModifiedAtLocal(LocalDateTime modifiedAtLocal) {
    this.modifiedAtLocal = modifiedAtLocal;
  }

  public String getPostContent() {
    return postContent;
  }

  public void setPostContent(String postContent) {
    this.postContent = postContent;
  }

  public String getPostTitle() {
    return postTitle;
  }

  public void setPostTitle(String postTitle) {
    this.postTitle = postTitle;
  }

  public String getPostSlug() {
    return postSlug;
  }

  public void setPostSlug(String postSlug) {
    this.postSlug = postSlug;
  }

  public String getPostStatus() {
    return postStatus;
  }

  public void setPostStatus(String postStatus) {
    this.postStatus = postStatus;
  }

  public String getPostType() {
    return postType;
  }

  public void setPostType(String postType) {
    this.postType = postType;
  }

  public Long getPostParent() {
    return postParent;
  }

  public void setPostParent(Long postParent) {
    this.postParent = postParent;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public String getPostMimeType() {
    return postMimeType;
  }

  public void setPostMimeType(String postMimeType) {
    this.postMimeType = postMimeType;
  }

  public Set<WPTermRelationships> getTermRelationships() {
    return termRelationships;
  }

  public void setTermRelationships(Set<WPTermRelationships> termRelationships) {
    this.termRelationships = termRelationships;
  }

  public Set<WPMeta> getPostMetadataSet() {
    return postMetadataSet;
  }

  public void setPostMetadataSet(Set<WPMeta> postMetadataSet) {
    this.postMetadataSet = postMetadataSet;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    WPost other = (WPost) obj;
    return Objects.equals(getId(), other.getId())
        && Objects.equals(getPostAuthor(), other.getPostAuthor())
        && Objects.equals(getPostContent(), other.getPostContent())
        && Objects.equals(getPostTitle(), other.getPostTitle())
        && Objects.equals(getPostSlug(), other.getPostSlug())
        && Objects.equals(getPostStatus(), other.getPostStatus())
        && Objects.equals(getPostType(), other.getPostType())
        && Objects.equals(getCreatedAtGMT(), other.getCreatedAtGMT())
        && Objects.equals(getCreatedAtLocal(), other.getCreatedAtLocal())
        && Objects.equals(getModifiedAtGMT(), other.getModifiedAtGMT())
        && Objects.equals(getModifiedAtLocal(), other.getModifiedAtLocal())
        && Objects.equals(getPostParent(), other.getPostParent())
        && Objects.equals(getGuid(), other.getGuid())
        && Objects.equals(getPostMimeType(), other.getPostMimeType())
        && termRelationships.containsAll(other.getTermRelationships())
        && postMetadataSet.containsAll(other.getPostMetadataSet());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        postAuthor,
        postContent,
        postTitle,
        postSlug,
        postStatus,
        postType,
        createdAtGMT,
        createdAtLocal,
        modifiedAtGMT,
        modifiedAtLocal,
        postParent,
        guid,
        postMimeType,
        termRelationships);
  }

  @Override
  public String toString() {
    return "WPost [id="
        + id
        + ", postAuthor="
        + postAuthor
        + ", createdAtGMT="
        + createdAtGMT
        + ", createdAtLocal="
        + createdAtLocal
        + ", modifiedAtGMT="
        + modifiedAtGMT
        + ", modifiedAtLocal="
        + modifiedAtLocal
        + ", postContent="
        + postContent
        + ", postTitle="
        + postTitle
        + ", postSlug="
        + postSlug
        + ", postStatus="
        + postStatus
        + ", postType="
        + postType
        + ", postParent="
        + postParent
        + ", guid="
        + guid
        + ", postMimeType="
        + postMimeType
        + ", wpTermRelationShips="
        + termRelationships.toString()
        + ", postMetadataSet="
        + postMetadataSet.stream().map(WPMeta::getMetaFieldKey).collect(Collectors.joining(","))
        + "]";
  }
}
