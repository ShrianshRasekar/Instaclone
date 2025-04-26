package com.Auth_service.service;


import com.Auth_service.entity.UserCredential;
import com.Auth_service.repo.UserCredentialRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public ResponseEntity<?> saveUser(UserCredential credential) {
        boolean userExists = repository.existsByNameAndEmail(credential.getName(), credential.getEmail());

        if (userExists) {
            return ResponseEntity.status(HttpStatus.SC_CONFLICT)
                    .body(Map.of("error", "User already exists with the same name and email."));
        }

        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User added to the system.");
        response.put("username", credential.getName());
        response.put("email", credential.getEmail());

        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }


    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public Date validateToken(String token) {
        return jwtService.validateToken(token);
    }
    
	

}