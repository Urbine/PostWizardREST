package net.ygbstudio.postdirector.entities;

// Jakarta imports
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbPropertyOrder;


@Entity
@Table(name = "`wp_posts`")
@JsonbPropertyOrder(value = {
		"ID",
		"postAuthor", 
		"postContent", 
		"postTitle", 
		"postSlug", 
		"postStatus", 
		"postType" }
)
public class WPost {
	
	@Id
	@Column(name = "ID", 
			insertable = false, 
			updatable = false) 
	private long ID;
	
	@Column(name = "post_author")
	@JsonbProperty("post_author")
	private long postAuthor;
	
	@Column(name = "post_content")
	@JsonbProperty("post_content")
	private String postContent;
	
	@Column(name = "post_title")
	@JsonbProperty("post_title")
	private String postTitle;
	
	@Column(name = "post_name")
	@JsonbProperty("post_name")
	private String postSlug;
	
	@Column(name = "post_status")
	@JsonbProperty("post_status")
	private String postStatus;
	
	@Column(name = "post_type")
	@JsonbProperty("post_type")
	private String postType;
	
	public WPost() {}

	public WPost(
			long iD, 
			long postAuthor, 
			String postContent, 
			String postTitle, 
			String postSlug, 
			String postStatus,
			String postType) {
		super();
		ID = iD;
		this.postAuthor = postAuthor;
		this.postContent = postContent;
		this.postTitle = postTitle;
		this.postSlug = postSlug;
		this.postStatus = postStatus;
		this.postType = postType;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getPostAuthor() {
		return postAuthor;
	}

	public void setPostAuthor(long postAuthor) {
		this.postAuthor = postAuthor;
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

	@Override
	public String toString() {
		return "WPost [ID=" + ID + ", postAuthor=" + postAuthor + ", postContent=" + postContent + ", postTitle="
				+ postTitle + ", postSlug=" + postSlug + ", postStatus=" + postStatus + ", postType=" + postType + "]";
	}
	
	
	
	
}
