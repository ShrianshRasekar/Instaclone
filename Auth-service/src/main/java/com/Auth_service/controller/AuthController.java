package com.Auth_service.controller;

import com.Auth_service.dto.AuthRequest;
import com.Auth_service.entity.UserCredential;
import com.Auth_service.service.Authservice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*") // Or your frontend port
public class AuthController {
    @Autowired
    private Authservice service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody UserCredential user) {
        try {
            return service.saveUser(user);  // directly return the response
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "An unexpected error occurred"));
        }
    }



    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), 
                    authRequest.getPassword()
                )
            );

            if (authenticate.isAuthenticated()) {
                String token = service.generateToken(authRequest.getUsername());

				/*
				 * Map<String, Object> response = new HashMap<>(); response.put("token", token);
				 * response.put("message", "Authentication successful");
				 * response.put("username", authRequest.getUsername());
				 */
                return ResponseEntity.ok(token);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of("error", "Invalid credentials"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("error", "Invalid credentials"));
        }
    }


    @GetMapping("/validate/{token}")
    public ResponseEntity<Map<String, String>> validateToken(@PathVariable("token") String token) {
        Map<String, String> response = new HashMap<>();

        try {
            Date expirationDate = service.validateToken(token);

            if (expirationDate == null) {
                response.put("message", "Token is invalid.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Date now = new Date();
            long diffInMillis = expirationDate.getTime() - now.getTime();

            if (diffInMillis <= 0) {
                response.put("message", "Token has expired.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;

            String timeLeft = (days > 0 ? days + " days " : "") +
                              (hours > 0 ? hours + " hours " : "") +
                              (minutes > 0 ? minutes + " min " : "");

            response.put("message", "Token is valid.");
            response.put("validTill", expirationDate.toString());
            response.put("timeRemaining", timeLeft.trim());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", "Malformed token.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "Token validation failed.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


}