package com.peninsula.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.peninsula.community.model.PostDetails;

import jakarta.transaction.Transactional;

public interface CommunityRepository extends JpaRepository<PostDetails, Long> {

	@Query("SELECT p.postId FROM PostDetails p WHERE p.userCredentials.userId = :userId AND p.status = 1")
	List<Long> findPostsByUserId(Long userId);

	@Query(value="select p from PostDetails p where p.postId = :postId and p.status = 1")
	PostDetails findByPostId(Long postId);
	
	@Query(value = "select p.postId from PostDetails p where p.privacy = 0 and p.status = 1")
	List<Long> getPublicPosts();

	@Query(value = "select p.postId from PostDetails p where  p.status = 1")
	List<Long> getAllPosts();

	@Modifying
	@Transactional
	@Query(value="UPDATE PostDetails p SET p.status = 0 WHERE p.postId = :postId")
	void deletePostById(Long postId);

}
