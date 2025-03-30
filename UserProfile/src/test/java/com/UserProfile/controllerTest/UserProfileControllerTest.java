package com.UserProfile.controllerTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.UserProfile.controller.ProfileController;
import com.UserProfile.entity.UserProfile;
import com.UserProfile.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController userProfileController;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @BeforeAll
    public static void init() {
        System.out.println("Before test");
        System.out.println("Started test: " + new Date());
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
    }

    @Test
    public void testGetUserProfiles() throws Exception {
        List<UserProfile> mockProfiles = Arrays.asList(
                new UserProfile(1L, "JohnDoe", "Johnathan Doe", "Software Developer", 50, 100, 200, 123, null),
                new UserProfile(2L, "JaneDoe", "Jane Doe", "Product Manager", 30, 150, 250, 124, null)
        );

        when(profileService.getUserProfiles()).thenReturn(mockProfiles);

        mockMvc.perform(get("/userprofile/profiles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(mockProfiles)));
    }

    @Test
    public void testGetUserProfileById() throws Exception {
        UserProfile mockProfile = new UserProfile(1L, "JohnDoe", "Johnathan Doe", "Software Developer", 50, 100, 200, 123, null);

        when(profileService.getUserProfile(1L)).thenReturn(mockProfile);

        mockMvc.perform(get("/userprofile/profileid/{pid}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(mockProfile)));
    }

    @Test
    public void testGetUserProfileByUsername() throws Exception {
        UserProfile mockProfile = new UserProfile(1L, "JohnDoe", "Johnathan Doe", "Software Developer", 50, 100, 200, 123, null);

        when(profileService.getUserProfileByUname("JohnDoe")).thenReturn(mockProfile);

        mockMvc.perform(get("/userprofile/{uname}", "JohnDoe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(mockProfile)));
    }

    @Test
    public void testAddUserProfile() throws Exception {
        UserProfile mockUserProfile = new UserProfile();
        mockUserProfile.setUname("JohnDoe");
        mockUserProfile.setBio("New bio");

        // Use when(...).thenReturn(...) instead of doNothing()
        when(profileService.addUserProfile(any(UserProfile.class)))
            .thenReturn(mockUserProfile);

        mockMvc.perform(post("/userprofile/addProfileInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"JohnDoe\", \"bio\":\"New bio\"}"))
                .andExpect(status().isCreated());

        verify(profileService, times(1)).addUserProfile(any(UserProfile.class));
    }


    @Test
    public void testUpdateUserProfileBio() throws Exception {
        UserProfile mockUserProfile = new UserProfile();
        mockUserProfile.setUname("JohnDoe");
        mockUserProfile.setBio("Updated bio");

        when(profileService.updateUserProfileBio(anyString(), anyString())).thenReturn(mockUserProfile);

        mockMvc.perform(patch("/userprofile/update-bio")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uname\":\"JohnDoe\", \"bio\":\"Updated bio\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User bio updated successfully"));

        verify(profileService, times(1)).updateUserProfileBio("JohnDoe", "Updated bio");
    }




    @Test
    public void testDeleteUserProfileByUname() throws Exception {
        // No need to use doNothing() if the method is void, just verify the call
        doAnswer(invocation -> null).when(profileService).deleteUserProfileByUname("JohnDoe");

        mockMvc.perform(delete("/userprofile/username/{uname}", "JohnDoe"))
                .andExpect(status().isOk())
                .andExpect(content().string("UserProfile deleted having username JohnDoe"));

        // Verify method was called
        verify(profileService, times(1)).deleteUserProfileByUname("JohnDoe");
    }


    @AfterAll
    public static void clean() {
        System.out.println("After test");
        System.out.println("Ended test: " + new Date());
    }
}
