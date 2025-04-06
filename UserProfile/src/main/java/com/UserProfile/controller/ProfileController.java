package com.UserProfile.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.UserProfile.entity.ProfilePicture;
import com.UserProfile.entity.UserProfile;
import com.UserProfile.exception.ProfileDetailAlreadyExist;
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
	
	private final String uploadDir = "uploads/";

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

		UserProfile userProfile = profileService.getUserProfile(pid);

		if (userProfile == null) {
			return ResponseEntity.notFound().build();
		}

		// Set profile picture URL if exists
		if (userProfile.getProfilePicture1() != null) {
			String imageUrl = "/userprofile/profilePicture/" + userProfile.getUname();
			userProfile.getProfilePicture1().setFilePath(imageUrl);
		}

		return ResponseEntity.ok(userProfile);
	}

	@GetMapping(path = "/{uname}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Cacheable(key = "#uname", value = "UserProfile", unless = "#result?.body?.followers != null && #result.body.followers > 200")
	public ResponseEntity<UserProfile> getUserProfileByUsername(@PathVariable String uname) {
		logger.info("Fetching user profile with username: {}", uname);

		UserProfile user = profileService.getUserProfileByUname(uname);

		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		// Set profile picture URL if exists
		if (user.getProfilePicture1() != null) {
			String imageUrl ="/userprofile/getImage/" +user.getProfilePicture1().getFileName();
			user.getProfilePicture1().setFilePath(imageUrl);
		}

		return ResponseEntity.ok(user);
	}

	@PostMapping("/addProfileInfo")
	public ResponseEntity<String> createUserProfile(@RequestParam("file") MultipartFile file,
			@RequestParam("uname") String uname, @RequestParam("fullName") String fullName,
			@RequestParam("bio") String bio, @RequestParam("posts") int posts, @RequestParam("followers") int followers,
			@RequestParam("following") int following, @RequestParam(value = "uid", required = false) Integer uid) { // Made
																													// uid
																													// Optional

		if (profileService.isUserProfilenameExistAlready(uname)) {
			throw new ProfileDetailAlreadyExist("UserProfile already exists with username " + uname);
		}

		// Validate file type
		List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/jpg");
		if (!allowedTypes.contains(file.getContentType())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type! Only JPG, PNG, GIF allowed.");
		}

		try {
			// Define upload directory
			String uploadDir = System.getProperty("user.dir") + "/uploads";
			Files.createDirectories(Paths.get(uploadDir)); // Creates directory if not exists

			// Generate unique file name
			String uniqueFileName = file.getOriginalFilename();
			String filePath = uploadDir + "/" + uniqueFileName;

			// Save file securely
			Path destinationPath = Paths.get(filePath);
			Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

			// Create ProfilePicture entity
			ProfilePicture profilePicture = new ProfilePicture();
			profilePicture.setFileName(file.getOriginalFilename());
			profilePicture.setFilePath(filePath);
			profilePicture.setFileType(file.getContentType());

			// Save profile picture first
			profileService.saveProfilePicture(profilePicture);

			// Set uid to null if not provided
			if (uid == null) {
				uid = 0; // You can also keep it `null` if the database allows it
			}

			// Create UserProfile entity
			UserProfile userProfile = new UserProfile(uid, uname, fullName, bio, posts, followers, following, uid,
					profilePicture);

			// Save user profile
			profileService.addUserProfile(userProfile);

			return ResponseEntity.ok("Profile added successfully with image: " + filePath);

		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());
		}
	}

	@GetMapping("/getImage/{fileName}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
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
		logger.info(message);
	}
}
