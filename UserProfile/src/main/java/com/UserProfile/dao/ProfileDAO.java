package com.UserProfile.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.UserProfile.entity.UserProfile;

import jakarta.transaction.Transactional;

@EnableJpaRepositories
public interface ProfileDAO extends JpaRepository<UserProfile, Long> {

	public static final String HASH_KEY = "UserProfile";

	@Query("select uname From UserProfile u WHERE u.uname=:un")
	public List<String> isUserProfilenameExist(@Param("un") String uname);

	@Query("select uname From UserProfile u WHERE u.uname=:un")
	public String isUserProfilenameExistAlready(@Param("un") String uname);

	@Query("select u From UserProfile u WHERE u.uname=:un")
	public UserProfile getUserProfileByUsername(@Param("un") String uname);

	@Modifying
	@Query("UPDATE UserProfile u SET u.bio=:bio WHERE u.uname=:un ")
	public int updateUserProfileBio(@Param("un") String username, @Param("bio") String bio);

	// --------------------------------------------------------------------------------------------------------------Delete

	@Query("DELETE FROM UserProfile u WHERE u.uname = :un")
	@Modifying
	@Transactional
	void deleteByUsername(@Param("un") String uname);

	@Modifying
	@Query("UPDATE UserProfile u SET u.followers = u.followers + :count WHERE u.uname = :uname AND u.followers < 1000")
	public void addFollower(@Param("uname") String uname, @Param("count") Long count);

	// ----------------------------- 🔹 NEWLY ADDED METHODS FOR PROFILE PICTURE
	// -----------------------------

	@Modifying
	@Transactional
	@Query("UPDATE UserProfile u SET u.profilePicturePath = :path WHERE u.username = :username")
	void updateProfilePicturePath(@Param("username") String username, @Param("path") String path);

	@Query("SELECT u.profilePicture.filePath FROM UserProfile u WHERE u.uname = :uname")
	String getProfilePicturePathByUsername(@Param("uname") String uname);

}
