package com.UserProfile.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.UserProfile.dao.ProfileDAO;
import com.UserProfile.dao.ProfilePictureRepository;
import com.UserProfile.exception.ProfileNotFoundException;

import jakarta.transaction.Transactional;

import com.UserProfile.entity.ProfilePicture;
import com.UserProfile.entity.UserProfile;

@Service
public class UserProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileDAO Profiledao;

	@Autowired
	private ProfilePictureRepository profilePictureRepository; // ✅ Correct repository

	// ALL GET
	// requests----------------------------------------------------------------GET----
	@Override
	public List<UserProfile> getUserProfiles() {
		// TODO Auto-generated method stub
		if (Profiledao.findAll().isEmpty()) {
			throw new ProfileNotFoundException("No UserProfile Exist till now");
		}
		return Profiledao.findAll();
	}

	@Override
	public UserProfile getUserProfile(Long pid) {
		if (Profiledao.findById(pid).isEmpty()) {
			throw new ProfileNotFoundException("UserProfile not exist with id " + pid);
		}
		System.out.println("called getUserProfile by ID from DATABASE");
		return Profiledao.findById(pid).get();
	}

	public List<String> isUserProfilenameExist(String ename) {
		if (Profiledao.isUserProfilenameExist(ename).isEmpty()) {
			throw new ProfileNotFoundException("UserProfile not exist with UserProfilename '" + ename + "' ");
		}
		List<String> u = Profiledao.isUserProfilenameExist(ename);
		/*
		 * List<String> UserProfilenames = u.stream().filter(e -> //
		 * e.equals(e.getUname()).collect(Collectors.toList()));
		 */
		return u;
	}

	public boolean isUserProfilenameExistAlready(String ename) {
		String s = Profiledao.isUserProfilenameExistAlready(ename);
		if (s == null) {
			s = "";
		}
		if (s.equals(ename)) {
			return true;
		} // List<String> UserProfilenames =
			// UserProfile.stream().filter(e -> //
			// e.equals(e.getUname()).collect(Collectors.toList()));
		return false;
	}

	@Override
	public UserProfile getUserProfileByUname(String uname) {

		System.out.println("called getUserProfileByUsername from DATABASE");
		return Profiledao.getUserProfileByUsername(uname);
	}

	/*
	 * public List<String> getAllUserProfilenames() { if
	 * (UserProfiledao.getAllUserProfilename().isEmpty()) { throw new
	 * UserProfileNotFoundException("No UserProfile Exist till now"); }
	 * 
	 * return UserProfiledao.getAllUserProfilename(); }
	 * 
	 * @Override public UserProfile getUserProfileByUnameAndPassword(String uname,
	 * String password) { // TODO Auto-generated method stub if (uname.isEmpty() ||
	 * password.isEmpty()) { throw new
	 * UserProfileDetailsMissingException("Enter All UserProfile details"); } if
	 * (UserProfiledao.getUserProfileByUnameAndPassword(uname, password) == null) {
	 * throw new
	 * UserProfileNotFoundException("Login Failed!! Enter Correct Details"); }
	 * 
	 * return Profiledao.getUserProfileByUnameAndPassword(uname, password); }
	 */
	/*
	 * @Override
	 * 
	 * @Query("select u From UserProfile u WHERE u.uname=:un and u.password=:pass")
	 * public UserProfile getUserProfileByUnameAndPassword(@Param("un")String
	 * uname, @Param("pass")String password) { // TODO Auto-generated method stub
	 * return UserProfile; }
	 */

	// ALL POST
	// requests-------------------------------------------------------------POST-------
	@Override
	public UserProfile addUserProfile(UserProfile UserProfile) {
		Profiledao.save(UserProfile);
		UserProfile.setPid(UserProfile.getUid());
		return UserProfile;
	}

	@Override
	public List<UserProfile> addUserProfiles(List<UserProfile> ls) {
		// l.addAll(ls);

		/*
		 * for (UserProfile UserProfile : ls) { if (UserProfile.getEmail().isEmpty() ||
		 * UserProfile.getFullName().isEmpty() || UserProfile.getUname().isEmpty() ||
		 * UserProfile.getPassword().isEmpty()) { throw new
		 * UserProfileDetailsMissingException("Enter All UserProfile details"); } }
		 */
		Profiledao.saveAll(ls);
		return ls;
	}

	// ALL PUT
	// requests-----------------------------------------------------------------PUT---
	@Override
	public UserProfile updateUserProfile(UserProfile UserProfile) {
		// TODO Auto-generated method stub
		Profiledao.save(UserProfile);

		return UserProfile;
	}
	// ALL Patch
	// requests-----------------------------------------------------------------Patch---

	@Transactional
	@Override
	public UserProfile updateUserProfileBio(String username, String bio) {
		if (Profiledao.isUserProfilenameExist(username).isEmpty()) {
			throw new ProfileNotFoundException("UserProfile not exist with UserProfilename '" + username + "' ");
		}
		Profiledao.updateUserProfileBio(username, bio);
		return Profiledao.getUserProfileByUsername(username);
	}

	@Transactional
	@CachePut(key = "#uname", value = "UserProfile")
	@Override
	public UserProfile updateUserProfileFollowers(String uname, Long count) {
		List<String> u = Profiledao.isUserProfilenameExist(uname);

		if (!u.isEmpty()) {
			Profiledao.addFollower(uname, count);

			// Fetch fresh data from DB and return it to update cache
			return Profiledao.getUserProfileByUsername(uname);
		} else {
			throw new ProfileNotFoundException("UserProfile does not exist with UserName '" + uname + "'");
		}
	}

	// ALL DELETE
	// requests----------------------------------------------------------------DELETE----
	@Override
	public String deleteUserProfile(Long pid) {
		// TODO Auto-generated method stub
		// UserProfile e=UserProfiledao.getById(eid);

		if (Profiledao.findById(pid).isEmpty()) {
			throw new ProfileNotFoundException("UserProfile not exist with id " + pid);
		}
		Profiledao.deleteById(pid);
		return "Deleted UserProfile with id " + pid;
	}

	@Override
	public String deleteUserProfileByUname(String uname) {
		// TODO Auto-generated method stub
		// UserProfile e=UserProfiledao.getById(eid);

		if (Profiledao.isUserProfilenameExistAlready(uname).isEmpty()) {
			throw new ProfileNotFoundException("UserProfile not exist with id " + uname);
		}

		Profiledao.deleteByUsername(uname);
		return "Deleted UserProfile with id " + uname;
	}

	@Transactional
	@Override
	public void updateUserProfilePicture(String uname, byte[] profilePicture) {
		if (Profiledao.isUserProfilenameExistAlready(uname) == null) {
			throw new ProfileNotFoundException("UserProfile does not exist with username '" + uname + "'");
		}

		// Define the file path inside the uploads folder
		String directoryPath = "uploads/";
		String fileName = uname + "_profile.jpg";
		Path filePath = Paths.get(directoryPath + fileName);

		try {
			// Ensure the directory exists
			Files.createDirectories(Paths.get(directoryPath));
			// Write the image to the directory
			Files.write(filePath, profilePicture);
		} catch (IOException e) {
			throw new RuntimeException("Error saving profile picture", e);
		}

		// Save only the file path in the database
		Profiledao.updateProfilePicturePath(uname, filePath.toString());
	}

	@Override
	public void saveProfilePicture(ProfilePicture profilePicture) {
		profilePictureRepository.save(profilePicture);
	}

	@Override
	public byte[] getUserProfilePicture(String uname) {
		String filePath = Profiledao.getProfilePicturePathByUsername(uname);
		if (filePath == null || filePath.isEmpty()) {
			throw new ProfileNotFoundException("Profile picture not found for username '" + uname + "'");
		}
		try {
			return Files.readAllBytes(Paths.get(filePath));
		} catch (IOException e) {
			throw new RuntimeException("Error reading profile picture", e);
		}
	}

}
