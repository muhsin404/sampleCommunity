package com.peninsula.community.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peninsula.community.service.ComService;


@RestController
public class ComController {
	
	@Autowired
	private ComService comService;
	
	@PostMapping("/uploadPost")
	public ResponseEntity<Map<String, Object>> uploadPost(
	        @RequestHeader("userToken") String userToken,
	        @RequestBody HashMap<String, Object> data) {
	    
	    Map<String, Object> response = comService.uploadPost(data, userToken);

	    if ("Success".equals(response.get("status"))) {
	    	
	        return ResponseEntity.ok(response);
	    } else {
	    	
	        return ResponseEntity.badRequest().body(response);
	    }
	}

	
	@GetMapping("/getPosts")
	public ResponseEntity<Map<String, Object>> getPosts(@RequestHeader("userToken") String userToken) {
	    // Call the service method to fetch the posts
	    Map<String, Object> response = comService.getPosts(userToken);

	    if ("Success".equals(response.get("status"))) {
	    	
	        return ResponseEntity.ok(response);
	    } else {

	    	return ResponseEntity.badRequest().body(response);
	    }
	}
	
	@GetMapping("/getPostOfUser")
	public ResponseEntity<Map<String, Object>> getPostOfAUser(@RequestHeader("userToken") String userToken,
																@RequestParam("userId") Long userId) {
		// Call the service method to fetch the posts
		Map<String, Object> response = comService.getPostOfAUser(userToken,userId);
		
		if ("Success".equals(response.get("status"))) {
			
			return ResponseEntity.ok(response);
		} else {
			
			return ResponseEntity.badRequest().body(response);
		}
	}

	
	
	@PostMapping("/deletePost")
	public ResponseEntity<Map<String, Object>> deletePost(@RequestHeader("userToken") String userToken,
	                                                      @RequestParam("postId") Long postId) {
	    // Call the service method to delete the post
	    Map<String, Object> response = comService.deletePost(userToken, postId);

	    if ("Success".equals(response.get("status"))) {
	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.badRequest().body(response);
	    }
	}


}
