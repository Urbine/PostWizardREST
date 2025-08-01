package net.ygbstudio.postdirector.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;


/**
 * Represents metadata associated with a WordPress post.
 * <p>
 * This entity maps to the {@code wp_postmeta} table and allows
 * reading and updating key-value metadata pairs for posts.
 */

@Entity 
@Table(name = "`wp_postmeta`")
@JsonbPropertyOrder(value = {"postID", "metaFieldKey", "metaFieldValue"})
public class WPMeta {
	
	
	@Id
	@Column(
			name = "meta_id", 
			insertable = false, 
			updatable = false
			)
	@JsonbTransient
    private Long metaID;

    /**
     * The post ID this metadata is associated with.
     */
    @Column(name = "post_id", nullable = false)
    @JsonbProperty("post_id")
    private Long postID;

	@Column(name = "meta_key")
	@JsonbProperty("meta_key")
    private String metaFieldKey;

    @Column(name = "meta_value", columnDefinition = "LONGTEXT")
    @JsonbProperty("meta_value")
    private String metaFieldValue;
    
    public WPMeta() {}

	public WPMeta(Long metaID, Long postID, String metaFieldKey, String metaFieldValue) {
		super();
		this.metaID = metaID;
		this.postID = postID;
		this.metaFieldKey = metaFieldKey;
		this.metaFieldValue = metaFieldValue;
	}


    public Long getMetaID() {
        return metaID;
    }

    public void setMetaID(Long metaID) {
        this.metaID = metaID;
    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public String getMetaFieldKey() {
        return metaFieldKey;
    }

    public void setMetaFieldKey(String metaFieldKey) {
        this.metaFieldKey = metaFieldKey;
    }

    public String getMetaFieldValue() {
        return metaFieldValue;
    }

    public void setMetaFieldValue(String metaFieldValue) {
        this.metaFieldValue = metaFieldValue;
    }

	@Override
	public String toString() {
		return "WPMeta [metaID=" + metaID + ", postID=" + postID + ", metaFieldKey=" + metaFieldKey
				+ ", metaFieldValue=" + metaFieldValue + "]";
	}
    
}
