package com.UserProfile.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.UserProfile.entity.ProfilePicture;

import jakarta.transaction.Transactional;

@EnableJpaRepositories(basePackages = "com.UserProfile.dao")
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {

	@Modifying
	@Transactional
	@Query("UPDATE UserProfile u SET u.profilePicture.filePath = :path WHERE u.uname = :uname")
	void updateProfilePicturePath(@Param("uname") String uname, @Param("path") String path);

	@Query("SELECT u.profilePicture.filePath FROM UserProfile u WHERE u.uname = :uname")
	String getProfilePicturePathByUsername(@Param("uname") String uname);

	@Query("SELECT u.profilePicture FROM UserProfile u WHERE u.uname = :uname")
	ProfilePicture findProfilePictureByUsername(@Param("uname") String uname);

}