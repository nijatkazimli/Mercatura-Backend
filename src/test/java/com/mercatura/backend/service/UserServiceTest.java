package com.mercatura.backend.service;

import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.dto.Responses.UserResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Image;
import com.mercatura.backend.repository.ImageRepository;
import com.mercatura.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllUsers_ShouldReturnUserResponses() {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setAuthorities(new HashSet<>());
        Page<ApplicationUser> userPage = new PageImpl<>(List.of(applicationUser));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        List<UserResponse> responses = userService.getAllUsers(0, 10);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(userRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getUserById_ShouldReturnUserResponse() {
        UUID userId = UUID.randomUUID();
        ApplicationUser user = new ApplicationUser();
        user.setAuthorities(new HashSet<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(userId);

        assertNotNull(response);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                userService.getUserById(userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void addProfileImage_ShouldReturnUUIDResponse() {
        UUID userId = UUID.randomUUID();
        String imageUrl = "https://example.com/image.jpg";
        ApplicationUser user = new ApplicationUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UUIDResponse response = userService.addProfileImage(userId, imageUrl);

        assertNotNull(response);
        verify(imageRepository).save(any(Image.class));
        verify(userRepository).save(user);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        String username = "testUser";
        ApplicationUser user = new ApplicationUser();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.loadUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        String username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername(username));
    }

    @Test
    void deleteUserById_ShouldDeleteUser() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_ShouldThrowException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                userService.deleteUserById(userId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
