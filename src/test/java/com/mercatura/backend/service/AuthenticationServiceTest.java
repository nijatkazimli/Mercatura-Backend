package com.mercatura.backend.service;

import com.mercatura.backend.dto.RequestBody.PasswordChangeBody;
import com.mercatura.backend.dto.Responses.AuthResponse;
import com.mercatura.backend.dto.Responses.RolesResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Role;
import com.mercatura.backend.repository.RoleRepository;
import com.mercatura.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

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
    void registerUser_ShouldReturnAuthResponse() {
        String username = "testuser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String role = "USER";
        Role userRole = new Role();
        userRole.setAuthority(role);

        ApplicationUser newUser = new ApplicationUser("Test", "User", username, encodedPassword, Set.of(userRole));

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleRepository.findByAuthority(role)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(ApplicationUser.class))).thenReturn(newUser);
        when(tokenService.generateJWT(any(Authentication.class))).thenReturn("token");

        AuthResponse response = authenticationService.registerUser("Test", "User", username, password, role);

        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void loginUser_ShouldReturnAuthResponse() {
        String username = "testuser";
        String password = "password";
        String token = "token";

        ApplicationUser user = new ApplicationUser();
        user.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        user.setUsername(username);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(tokenService.generateJWT(any(Authentication.class))).thenReturn(token);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        AuthResponse response = authenticationService.loginUser(username, password);

        assertNotNull(response);
        assertEquals(token, response.getToken());
    }

    @Test
    void changePassword_ShouldReturnSuccessMessage() {
        String username = "testuser";
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        PasswordChangeBody passwordChangeBody = new PasswordChangeBody(username, currentPassword, newPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        String result = authenticationService.changePassword(passwordChangeBody);

        assertEquals("Password change successful.", result);
        verify(userRepository).save(user);
    }

    @Test
    void getAllRolesExceptAdmin_ShouldReturnRolesResponse() {
        Role userRole = new Role();
        userRole.setAuthority("USER");

        Role adminRole = new Role();
        adminRole.setAuthority("ADMIN");

        when(roleRepository.findAll()).thenReturn(List.of(userRole, adminRole));

        RolesResponse response = authenticationService.getAllRolesExceptAdmin();

        assertNotNull(response);
        assertEquals(1, response.getRoles().size());
        assertEquals("USER", response.getRoles().get(0));
    }
}
