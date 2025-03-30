package com.Auth_service.service;


import com.Auth_service.entity.UserCredential;
import com.Auth_service.repo.UserCredentialRepository;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Authservice {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    public ResponseEntity<String> saveUser(UserCredential credential) {
    	boolean userExists = repository.existsByNameAndEmail(credential.getName(), credential.getEmail());
        if (userExists) {
        	return ResponseEntity.status(HttpStatus.SC_CONFLICT)
                    .body("User already exists with the same name and email.");
        }
    	
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return ResponseEntity.status(HttpStatus.SC_CREATED)
                .body("User added to the system.");
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


}