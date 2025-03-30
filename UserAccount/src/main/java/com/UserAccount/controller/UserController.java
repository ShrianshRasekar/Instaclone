package com.UserAccount.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.UserAccount.exception.UserDetailAlreadyExist;
import com.UserAccount.pojo.User;
import com.UserAccount.service.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
	@Autowired
	private UserService userService;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);


	// ALL GET
	// requests--------------------------------------------------------***GET**------------
	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE,path = "users")
	public ResponseEntity<List<User>> getUsers() {
		logger.info("Getting all users");
		return ResponseEntity.of(Optional.of(userService.getUsers()));

	}
	
	int retryCount=1;
	@GetMapping(path="/userid/{uid}",produces=MediaType.APPLICATION_JSON_VALUE)
//	@CircuitBreaker(name="userProfileBreaker",fallbackMethod = "profileServiceFallback")
	@Retry(name="userProfileBreaker",fallbackMethod = "profileServiceFallback")
	public User getUser(@PathVariable Long uid) {
		/*
		 * User user=userService.getUser(uid); if(user==null) { return
		 * ResponseEntity.status(HttpStatus.NOT_FOUND).build(); } return
		 * ResponseEntity.of(Optional.of(user));
		 */
		logger.info("Retry Count :-> "+retryCount);
		++retryCount;
		logger.info("Get user with id "+uid);
		return userService.getUser(uid);
	}	
	
	public User profileServiceFallback(Long uid, Exception ex) {
		logger.info("Fallback executed because service is down ",ex.getMessage());
		User user=new User(1,"dummy","Dummy name","dummy@mailcom","password",null);
		return user;
	}

	@GetMapping(path="/{uname}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> isUsernameExist(@Validated @PathVariable String uname) {
		List<String> usernames = userService.isUsernameExist(uname);

		return ResponseEntity.of(Optional.of(usernames));
	}

	@GetMapping(path="/AllUsernames",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> getAllUsernames() {
		return ResponseEntity.of(Optional.of(userService.getAllUsernames()));
	}

	// -----------------------------------------------------------------------------------------------------LOGIN
	// REQUESTS---------
	//
	// using pathvariable
	// http://localhost:8000/user/login/me@15/me1115
	@GetMapping(path="/login/{uname}/{password}",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> getUserForLogin(@Validated @PathVariable String uname, @Validated @PathVariable String password) {
		User user = userService.getUserByUnameAndPassword(uname, password);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.of(Optional.of(user));

	}

	// using request Param
	// http://localhost:8000/user/login/user?uname=me@15&password=me1115
	@GetMapping(path="/login/user",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUserForLoginRp(@Validated @RequestParam String uname, @Validated @RequestParam String password) {
		String s = "Login Successful";
		// String f="Login Failed!! Enter Correct Details";
		User user = userService.getUserByUnameAndPassword(uname, password);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.of(Optional.of(s));

	}

	// ALL POST
	// requests---------------------------------------------------------***POST**-----SIGNUP------
	@PostMapping(path="/signUp",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addUser(@Validated @RequestBody User User) {
		/*
		 * String s="Signup Successful"; String
		 * f="Something went Wrong, Please try again"; User u=null; try {
		 */
		String s = "Signup Successful";
		if(userService.isUsernameExistAlready(User.getUname())) {
			throw new UserDetailAlreadyExist("User already exist with username " + User.getUname());
		}
		userService.addUser(User);
		return ResponseEntity.status(HttpStatus.CREATED).body(s);
		/*
		 * } catch (Exception e) { // TODO: handle exception e.printStackTrace(); return
		 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(f); }
		 */

	}

	@PostMapping(path="/addUsers",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addUsers(@Validated @RequestBody List<User> ls) {
		userService.addUsers(ls);
		String s = "Users added";
		return ResponseEntity.status(HttpStatus.CREATED).body(s);

	}

	// ALL PUT
	// requests----------------------------------------------------------***PUT**----------
	@PutMapping(path="/updateUser")
	public ResponseEntity<String> updateUser(@Validated @RequestBody User User) {
		String s = "Users updated";
		userService.updateUser(User);
		return ResponseEntity.status(HttpStatus.CREATED).body(s);

	}

	// ALL Delete
	// requests-------------------------------------------------------***Delete**-------------
	@DeleteMapping("/{uid}")
	public ResponseEntity<String> deleteUser(@PathVariable Long uid) {
		
		String s = userService.deleteUser(uid);
		return ResponseEntity.status(HttpStatus.OK).body(s);

	}
	
	@DeleteMapping("/username/{uname}")
	public ResponseEntity<String> deleteUserByUsername(@PathVariable String uname) {
		
		String s = userService.deleteUserByUname(uname);
		return ResponseEntity.status(HttpStatus.OK).body(s);

	}
	
	
	
}