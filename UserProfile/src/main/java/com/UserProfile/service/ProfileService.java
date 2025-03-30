package com.UserProfile.service;

import java.util.List;

import com.UserProfile.entity.ProfilePicture;
import com.UserProfile.entity.UserProfile;

public interface ProfileService {

	public List<UserProfile> getUserProfiles();

	public UserProfile getUserProfile(Long pid);

	public List<String> isUserProfilenameExist(String uname);

	public boolean isUserProfilenameExistAlready(String uname);

	public UserProfile getUserProfileByUname(String uname);

	public UserProfile addUserProfile(UserProfile UserProfile);

	public List<UserProfile> addUserProfiles(List<UserProfile> ls);

	public UserProfile updateUserProfile(UserProfile UserProfile);

	public UserProfile updateUserProfileBio(String username, String bio);

	public String deleteUserProfile(Long pid);

	public String deleteUserProfileByUname(String uname);

	public UserProfile updateUserProfileFollowers(String uname, Long count);

	// ---------------------------- 🔹 NEWLY ADDED METHODS FOR PROFILE PICTURE
	// ----------------------------
	public void updateUserProfilePicture(String uname, byte[] profilePicture);

	void saveProfilePicture(ProfilePicture profilePicture);

	public byte[] getUserProfilePicture(String uname);
}
