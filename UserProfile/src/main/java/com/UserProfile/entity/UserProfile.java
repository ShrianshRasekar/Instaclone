package com.UserProfile.entity;

import java.util.Arrays;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "uprofiles")
public class UserProfile {

	@jakarta.persistence.Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long pid;

	private String uname;

	/*
	 * @NotEmpty
	 * 
	 * @Size(min=2,message = "FullName must have atleast 2 characters")
	 */
	private String fullName;

	private String bio;

	private long posts;

	private long followers;

	private long following;

	private long uid;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "profile_picture_id", referencedColumnName = "id")
	private ProfilePicture profilePicture;

	// Getters and Setters
	public ProfilePicture getProfilePicture1() {
		return profilePicture;
	}

	public void setProfilePicture(ProfilePicture profilePicture) {
		this.profilePicture = profilePicture;
	}

	public UserProfile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserProfile(long pid, String uname, String fullName, String bio, long posts, long followers, long following,
			long uid, ProfilePicture profilePicture) {
		super();
		this.pid = pid;
		this.uname = uname;
		this.fullName = fullName;
		this.bio = bio;
		this.posts = posts;
		this.followers = followers;
		this.following = following;
		this.uid = uid;
		this.profilePicture = profilePicture;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public long getPosts() {
		return posts;
	}

	public void setPosts(long posts) {
		this.posts = posts;
	}

	public long getFollowers() {
		return followers;
	}

	public void setFollowers(long followers) {
		this.followers = followers;
	}

	public long getFollowing() {
		return following;
	}

	public void setFollowing(long following) {
		this.following = following;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "UserProfile [pid=" + pid + ", uname=" + uname + ", fullName=" + fullName + ", bio=" + bio + ", posts="
				+ posts + ", followers=" + followers + ", following=" + following + ", uid=" + uid + ", profilePicture="
				+ profilePicture + "]";
	}

}
