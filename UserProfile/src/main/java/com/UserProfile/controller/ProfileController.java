package com.UserProfile.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.UserProfile.entity.ProfilePicture;
import com.UserProfile.entity.UserProfile;
import com.UserProfile.exception.ProfileNotFoundException;
import com.UserProfile.service.ProfileService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/userprofile")
@CrossOrigin(origins = "http://localhost:3000") // Configurable CORS
@EnableCaching
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

	static {
		File directory = new File(UPLOAD_DIR);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@GetMapping(path = "/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<UserProfile>> getAllUserProfiles() {
		logger.info("Fetching all user profiles");
		List<UserProfile> profiles = profileService.getUserProfiles();
		return profiles.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(profiles);
	}

	@GetMapping(path = "/profileid/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Cacheable(key = "#pid", value = "UserProfile")
	public ResponseEntity<UserProfile> getUserProfileById(@PathVariable Long pid) {
		logger.info("Fetching user profile with ID: {}", pid);
		UserProfile user = profileService.getUserProfile(pid);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@GetMapping(path = "/{uname}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Cacheable(key = "#uname", value = "UserProfile", unless = "#result.followers > 200")
	public ResponseEntity<UserProfile> getUserProfileByUsername(@PathVariable String uname) {
		logger.info("Fetching user profile with username: {}", uname);
		UserProfile user = profileService.getUserProfileByUname(uname);
		return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
	}

	@PostMapping("/addProfileInfo")
	public ResponseEntity<String> createUserProfile(@RequestParam("file") MultipartFile file,
			@RequestParam("uname") String uname, @RequestParam("fullName") String fullName,
			@RequestParam("bio") String bio, @RequestParam("posts") int posts, @RequestParam("followers") int followers,
			@RequestParam("following") int following, @RequestParam("uid") int uid) {

		try {
			File uploadDir = new File("uploads/");
			if (!uploadDir.exists()) {
				uploadDir.mkdirs();
			}

			// Save file locally
			String filePath = "uploads/" + file.getOriginalFilename();
			File destinationFile = new File(filePath);
			file.transferTo(destinationFile);

			// Create ProfilePicture entity
			ProfilePicture profilePicture = new ProfilePicture();
			profilePicture.setFileName(file.getOriginalFilename());
			profilePicture.setFilePath(filePath);
			profilePicture.setFileType(file.getContentType());
			profileService.saveProfilePicture(profilePicture); // Save profile picture

			// Create UserProfile entity
			UserProfile userProfile = new UserProfile(uid, uname, fullName, bio, posts, followers, following,
					uid, profilePicture);
			profileService.addUserProfile(userProfile);

			return ResponseEntity.ok("Profile added successfully with image: " + filePath);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}
	}

	@GetMapping("/profilePicture/{uname}")
	public ResponseEntity<byte[]> getProfilePicture(@PathVariable String uname) {
		try {
			byte[] imageData = profileService.getUserProfilePicture(uname);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
		} catch (ProfileNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping(path = "/addUserProfiles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createMultipleProfiles(@Validated @RequestBody List<UserProfile> users) {
		profileService.addUserProfiles(users);
		logger.info("User profiles added successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body("UserProfiles added");
	}

	@PutMapping(path = "/updateUserProfile", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserProfile> updateUserProfile(@Validated @RequestBody UserProfile userProfile) {
		UserProfile updatedUser = profileService.updateUserProfile(userProfile);
		return ResponseEntity.ok(updatedUser);
	}

	@PatchMapping("/update-bio")
	public ResponseEntity<String> updateUserBio(@RequestBody Map<String, String> updates) {
		String username = updates.get("uname");
		String bio = updates.get("bio");

		if (username == null || username.isBlank()) {
			return ResponseEntity.badRequest().body("Username must be provided");
		}
		if (bio == null || bio.isBlank()) {
			return ResponseEntity.badRequest().body("Bio cannot be empty");
		}

		try {
			profileService.updateUserProfileBio(username, bio);
			logger.info("Updated bio for user: {}", username);
			return ResponseEntity.ok("User bio updated successfully");
		} catch (Exception e) {
			logger.error("Error updating bio for user {}: {}", username, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update bio");
		}
	}

	@PatchMapping("/addFollowers/{username}/{count}")
	public ResponseEntity<UserProfile> updateUserFollowers(@PathVariable String username, @PathVariable Long count) {
		UserProfile updatedUser = profileService.updateUserProfileFollowers(username, count);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{pid}")
	public ResponseEntity<String> deleteUserProfileById(@PathVariable Long pid) {
		profileService.deleteUserProfile(pid);
		logger.info("User profile deleted with ID: {}", pid);
		return ResponseEntity.ok("UserProfile deleted having ID " + pid);
	}

	@DeleteMapping("/username/{uname}")
	@CacheEvict(key = "#uname", value = "UserProfile")
	public ResponseEntity<String> deleteUserProfileByUsername(@PathVariable String uname) {
		profileService.deleteUserProfileByUname(uname);
		logger.info("User profile deleted with username: {}", uname);
		return ResponseEntity.ok("UserProfile deleted having username " + uname);
	}

	@Value("${message}")
	private String message;

	@PostConstruct
	public void printMessage() {
		logger.info("Application started at {} - {}", LocalDateTime.now(), "Message Loaded");
	}
}
