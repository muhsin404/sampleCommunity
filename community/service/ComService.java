package com.peninsula.community.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peninsula.common.repository.UserCredentialRepository;
import com.peninsula.common.utils.Utils;
import com.peninsula.community.model.PostDetails;
import com.peninsula.community.repository.CommunityRepository;
import com.peninsula.registration.model.UserCredentials;
import com.peninsula.registration.service.RegistrationService;

@Service
public class ComService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	private Utils utils;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private UserCredentialRepository userCredentialRepository;

	public Map<String, Object> uploadPost(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Validate input data
			if (data.get("locationPath") == null || data.get("locationPath").toString().isEmpty()
					|| data.get("caption") == null || data.get("caption").toString().isEmpty()
					|| data.get("privacy") == null || data.get("privacy").toString().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "All parameters are required");
				return response;
			}

//			boolean isNullOrEmpty = data.values().stream().anyMatch(v -> v == null || v.toString().isEmpty()||v.equals(""));
//			if (isNullOrEmpty) {
//				response.put("status", "Error");
//				response.put("message", "All parameters are required");
//				return response;
//			}

			// Verify user
			Map<String, Object> verificationResponse = utils.verifyUser(userToken);
			if (verificationResponse.get("status") == null || verificationResponse.get("status").equals("Error")) {
				return utils.buildErrorResponse("User verification failed");
			}

			int userRole = Integer.parseInt(verificationResponse.get("userRole").toString());
			if (userRole == 0 || userRole == 5) {
				return utils.buildErrorResponse("User not authorized");
			}

			Long userId = Long.parseLong(verificationResponse.get("userId").toString());
			UserCredentials user = userCredentialRepository.findByUserId(userId);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User credentials not found");
				return response;
			}

			// Create post
			PostDetails postDetails = new PostDetails();
			postDetails.setCaption(data.get("caption").toString());
			postDetails.setLocationPath(data.get("locationPath").toString());
			postDetails.setPrivacy(Integer.parseInt(data.get("privacy").toString()));
			postDetails.setUserCredentials(user);
			postDetails.setStatus(1);

			// Save post to repository
			communityRepository.save(postDetails);

			// Success response
			response.put("status", "Success");
			response.put("message", "Post uploaded successfully");
		} catch (NumberFormatException e) {
			response.put("status", "Error");
			response.put("message", "Invalid number format in input data");
		} catch (RuntimeException e) {
			response.put("status", "Error");
			response.put("message", "An unexpected error occurred: " + e.getMessage());
		} catch (Exception e) {
			response.put("status", "Error");
			response.put("message", "An error occurred: " + e.getMessage());
		}

		return response;
	}

	public Map<String, Object> getPosts(String userToken) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Verify user
			Map<String, Object> verificationResponse = utils.verifyUser(userToken);
			if (verificationResponse.get("status") == null || verificationResponse.get("status").equals("Error")) {
				return utils.buildErrorResponse("User verification failed");
			}

			int userRole = Integer.parseInt(verificationResponse.get("userRole").toString());
			List<Long> postIds;

			// Fetch posts based on user role
			if (userRole == 1 || userRole == 3 || userRole == 4) {
				postIds = communityRepository.getAllPosts(); // Fetching all posts for authorized roles
			} else {
				postIds = communityRepository.getPublicPosts(); // Fetching public posts for others
			}

			// Generate post cards based on post IDs
			List<Map<String, Object>> postCards = postCards(postIds);

			// Build success response
			response.put("status", "Success");
			response.put("posts", postCards);
		} catch (NumberFormatException e) {
			response.put("status", "Error");
			response.put("message", "Invalid number format in user data");
		} catch (RuntimeException e) {
			response.put("status", "Error");
			response.put("message", "An unexpected error occurred: " + e.getMessage());
		} catch (Exception e) {
			response.put("status", "Error");
			response.put("message", "An error occurred: " + e.getMessage());
		}

		return response;
	}


	public Map<String, Object> getPostOfAUser(String userToken, Long userId) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // Verify user
	        Map<String, Object> verificationResponse = utils.verifyUser(userToken);
	        if (verificationResponse.get("status") == null || verificationResponse.get("status").equals("Error")) {
	            return utils.buildErrorResponse("User verification failed");
	        }

	        // Extract authenticated user details
	        Long authenticatedUserId = Long.parseLong(verificationResponse.get("userId").toString());
	        int userRole = Integer.parseInt(verificationResponse.get("userRole").toString());

	        // Authorization check: allow if the user is accessing their own posts or has a privileged role
	        if (!authenticatedUserId.equals(userId) && !(userRole == 1 || userRole == 3 || userRole == 4)) {
	            response.put("status", "Error");
	            response.put("message", "You are not authorized to access this user's posts.");
	            return response;
	        }

	        // Fetch posts for the specific user
	        List<Long> postIds = communityRepository.findPostsByUserId(userId);

	        // Check if no posts are found
	        if (postIds == null || postIds.isEmpty()) {
	            response.put("status", "Success");
	            response.put("message", "No posts have been uploaded by this user.");
	            //response.put("posts", new ArrayList<>()); // Return an empty list for posts
	            return response;
	        }

	        // Generate post cards based on post IDs
	        List<Map<String, Object>> postCards = postCards(postIds);

	        // Build success response
	        response.put("status", "Success");
	        response.put("posts", postCards);
	    } catch (NumberFormatException e) {
	        response.put("status", "Error");
	        response.put("message", "Invalid user ID or role format.");
	    } catch (RuntimeException e) {
	        response.put("status", "Error");
	        response.put("message", "An unexpected error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        response.put("status", "Error");
	        response.put("message", "An error occurred while fetching posts: " + e.getMessage());
	    }

	    return response;
	}
	
	
	
	public Map<String, Object> deletePost(String userToken, Long postId) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // Verify user
	        Map<String, Object> verificationResponse = utils.verifyUser(userToken);
	        if (verificationResponse.get("status") == null || verificationResponse.get("status").equals("Error")) {
	            return utils.buildErrorResponse("User verification failed");
	        }

	        // Extract authenticated user details
	        Long authenticatedUserId = Long.parseLong(verificationResponse.get("userId").toString());
	        int userRole = Integer.parseInt(verificationResponse.get("userRole").toString());

	        // Fetch the post
	        PostDetails post = communityRepository.findByPostId(postId);
	        
	        // Check if post exists
	        if (post == null) {
	            response.put("status", "Error");
	            response.put("message", "Post not found for deletion..!");
	            return response;
	        }

	        // Authorization check: allow if the user is the post owner or is an admin
	        if (!authenticatedUserId.equals(post.getUserCredentials().getUserId()) && userRole != 1) {
	            response.put("status", "Error");
	            response.put("message", "You are not authorized to delete this post.");
	            return response;
	        }

	        // Delete the post
	        communityRepository.deletePostById(postId);

	        // Build success response
	        response.put("status", "Success");
	        response.put("message", "Post has been deleted successfully.");
	    } catch (NumberFormatException e) {
	        response.put("status", "Error");
	        response.put("message", "Invalid user ID or role format.");
	    } catch (RuntimeException e) {
	        response.put("status", "Error");
	        response.put("message", "An unexpected error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        response.put("status", "Error");
	        response.put("message", "An error occurred while deleting the post: " + e.getMessage());
	    }

	    return response;
	}



	public List<Map<String, Object>> postCards(List<Long> postIds) {
		List<Map<String, Object>> postCards = new ArrayList<>();
		
		for (Long postId : postIds) {
			PostDetails post = communityRepository.findById(postId)
					.orElseThrow(() -> new RuntimeException("Post not found for ID: " + postId));
			
			Map<String, Object> postCard = new HashMap<>();
			postCard.put("postId", post.getPostId());
			postCard.put("caption", post.getCaption());
			postCard.put("locationPath", post.getLocationPath());
			postCard.put("userName", post.getUserCredentials().getPeninsulaId()); // for getting the username, can
			// change to anything else like
			// userId, full name ....
			postCard.put("privacy", post.getPrivacy());
			postCard.put("createdAt", post.getCreatedAt());
			
			postCards.add(postCard);
		}
		
		return postCards;
	}
}
