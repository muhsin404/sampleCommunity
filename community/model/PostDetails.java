package com.peninsula.community.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.peninsula.registration.model.UserCredentials;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	private String caption;
	private String locationPath;
	private int privacy;
	private int status;
	



	@CreationTimestamp
	private LocalDateTime createdAt;
	
	
	
	
	public PostDetails( String caption, String locationPath, int privacy, LocalDateTime createdAt,
			UserCredentials userCredentials) {
		super();
		this.caption = caption;
		this.locationPath = locationPath;
		this.privacy = privacy;
		this.createdAt = createdAt;
		this.userCredentials = userCredentials;
	}
	
	

	public PostDetails() {
		super();
	}



	@ManyToOne
	@JoinColumn(name = "userId", referencedColumnName = "userId")
	private UserCredentials userCredentials;

	
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getLocationPath() {
		return locationPath;
	}

	public void setLocationPath(String locationPath) {
		this.locationPath = locationPath;
	}

	public int getPrivacy() {
		return privacy;
	}

	public void setPrivacy(int privacy) {
		this.privacy = privacy;
	}


	public UserCredentials getUserCredentials() {
		return userCredentials;
	}

	public void setUserCredentials(UserCredentials userCredentials) {
		this.userCredentials = userCredentials;
	}

	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
