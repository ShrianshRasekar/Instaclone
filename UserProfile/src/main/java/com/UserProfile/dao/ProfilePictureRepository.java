package com.UserProfile.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.UserProfile.entity.ProfilePicture;

@EnableJpaRepositories
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {
}