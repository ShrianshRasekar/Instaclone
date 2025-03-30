package com.UserAccount;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.UserAccount.controller.UserController;
import com.UserAccount.dao.UserDAO;
import com.UserAccount.pojo.User;
import com.UserAccount.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private UserService userService;
	
	@Mock
	private UserDAO userDao;
	
	@InjectMocks
	private UserController userController;
	
	ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();
    
    @BeforeClass
    public static void init() {
        System.out.println("Before test");
        System.out.println("Started test: " + new Date());
    }
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
    
    @Test
    public void testGetUser() throws Exception {
        // Arrange
        List<User> mockProfiles = Arrays.asList(
                new User(1, "shrianshr", "Shriansh R", "shri@mail.com", "shriansh",null),
                new User(2, "shriansjhhr", "Johnathan Dkhkhoe", "shrivhkh@mail.com", "shrfjiansh",null)
        );

        when(userService.getUsers()).thenReturn(mockProfiles);

        // Act & Assert
        mockMvc.perform(get("/user/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[" +
                	    "{\"uid\":1,\"uname\":\"shrianshr\",\"fullName\":\"Shriansh R\",\"email\":\"shri@mail.com\",\"password\":\"shriansh\",\"userprofile\":null}," +
                	    "{\"uid\":2,\"uname\":\"shriansjhhr\",\"fullName\":\"Johnathan Dkhkhoe\",\"email\":\"shrivhkh@mail.com\",\"password\":\"shrfjiansh\",\"userprofile\":null}" +
                	    "]"));
    }
    
    @AfterClass
    public static void clean() {
        System.out.println("After test");
        System.out.println("Ended test: " + new Date());
    }
    
}
