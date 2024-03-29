package com.UserAccount.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.UserAccount.dao.UserDAO;
import com.UserAccount.exception.UserDetailsMissingException;
import com.UserAccount.exception.UserNotFoundException;
import com.UserAccount.pojo.User;
import com.UserAccount.pojo.UserProfile;

@Service
public class UserServiceImpl implements UserService {

	
	private UserDAO Userdao;
	
	private ProfileClient profileClient;
	
	 

	public UserServiceImpl(UserDAO userdao, ProfileClient profileClient) {
		super();
		Userdao = userdao;
		this.profileClient = profileClient;
	}

	// ALL GET
	// requests----------------------------------------------------------------GET----
	@Override
	public List<User> getUsers() {
		// TODO Auto-generated method stub
		if (Userdao.findAll().isEmpty()) {
			throw new UserNotFoundException("No User Exist till now");
		}
		List<User> users=Userdao.findAll();
		List<User> newUsersList=users.stream().map(user->{user.setUserprofile(profileClient.getUserProfileOfUser(user.getUid()));
		return user;
		}).collect(Collectors.toList());
		return newUsersList;
	}

	@Override	
	public User getUser(Long uid) {
		/*
		 * if (Userdao.findById(uid).isEmpty()) { throw new
		 * UserNotFoundException("User not exist with id " + uid); }
		 */
		User user=Userdao.findById(uid).orElseThrow(()->new UserNotFoundException("User not exist with id " + uid));
		
		user.setUserprofile(profileClient.getUserProfileOfUser(user.getUid()));
		return user;
	}

	public List<String> isUsernameExist(String ename) {
		if (Userdao.isUsernameExist(ename).isEmpty()) {
			throw new UserNotFoundException("User not exist with username '" + ename + "' ");
		}
		List<String> u = Userdao.isUsernameExist(ename);
		// List<String> Usernames = User.stream().filter(e ->
		// e.equals(e.getUname()).collect(Collectors.toList()));
		return u;
	}
	
	public boolean isUsernameExistAlready(String ename) {
		String s=Userdao.isUsernameExistAlready(ename);
		if(s==null) {
			s="";
		}
		if(s.equals(ename)) {
			return true;
		}
		// List<String> Usernames = User.stream().filter(e ->
		// e.equals(e.getUname()).collect(Collectors.toList()));
		return false;
	}

	public List<String> getAllUsernames() {
		if (Userdao.getAllUsername().isEmpty()) {
			throw new UserNotFoundException("No User Exist till now");
		}

		return Userdao.getAllUsername();
	}

	@Override
	public User getUserByUnameAndPassword(String uname, String password) {
		// TODO Auto-generated method stub
		if (uname.isEmpty() || password.isEmpty()) {
			throw new UserDetailsMissingException("Enter All User details");
		}
		if (Userdao.getUserByUnameAndPassword(uname, password) == null) {
			throw new UserNotFoundException("Login Failed!! Enter Correct Details");
		}

		return Userdao.getUserByUnameAndPassword(uname, password);
	}
	/*
	 * @Override
	 * 
	 * @Query("select u From User u WHERE u.uname=:un and u.password=:pass") public
	 * User getUserByUnameAndPassword(@Param("un")String uname, @Param("pass")String
	 * password) { // TODO Auto-generated method stub return User; }
	 */

	// ALL POST
	// requests-------------------------------------------------------------POST-------
	@Override
	public User addUser(User user) {
		
		Userdao.save(user);
		return user;
	}

	@Override
	public List<User> addUsers(List<User> ls) {
		// l.addAll(ls);

		/*
		 * for (User user : ls) { if (user.getEmail().isEmpty() ||
		 * user.getFullName().isEmpty() || user.getUname().isEmpty() ||
		 * user.getPassword().isEmpty()) { throw new
		 * UserDetailsMissingException("Enter All User details"); } }
		 */
		Userdao.saveAll(ls);
		return ls;
	}

	// ALL PUT
	// requests-----------------------------------------------------------------PUT---
	@Override
	public User updateUser(User user) {
		// TODO Auto-generated method stub			
		
		  User updatedUser = Userdao.save(user);
		  updatedUser.setUserprofile(profileClient.updateUserProfileOfUser(user.
		  getUserprofile()));
		  return updatedUser;
		 
		
	}

	// ALL DELETE
	// requests----------------------------------------------------------------DELETE----
	@Override
	public String deleteUser(Long uid) {
		// TODO Auto-generated method stub
		// User e=Userdao.getById(eid);

		if (Userdao.findById(uid).isEmpty()) {
			throw new UserNotFoundException("User not exist with id " + uid);
		}
		Userdao.deleteById(uid);
		return "Deleted User with id " + uid;
	}

}
