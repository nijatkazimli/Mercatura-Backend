package com.mercatura.backend.service;

import com.mercatura.backend.dto.Responses.AuthResponse;
import com.mercatura.backend.dto.RequestBody.PasswordChangeBody;
import com.mercatura.backend.dto.Responses.RolesResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Role;
import com.mercatura.backend.repository.RoleRepository;
import com.mercatura.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthResponse registerUser(String name, String surname, String username, String password, String role) {
        String encoded = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority(role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        ApplicationUser newUser = userRepository.save(
                new ApplicationUser(name, surname, username, encoded, userRoles)
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, userRoles);
        String token = tokenService.generateJWT(authentication);

        return new AuthResponse(newUser.getId(), token);
    }

    public AuthResponse loginUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenService.generateJWT(authentication);

            if (userRepository.findByUsername(username).isPresent()) {
                return new AuthResponse(userRepository.findByUsername(username).get().getId(), token);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found");
            }
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    public String changePassword(PasswordChangeBody passwordResetBody) {
        try {
            ApplicationUser user = userRepository.findByUsername(passwordResetBody.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found"));
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(passwordResetBody.getUsername(), passwordResetBody.getCurrentPassword())
            );
            String newEncodedPassword = passwordEncoder.encode(passwordResetBody.getNewPassword());
            user.setPassword(newEncodedPassword);
            userRepository.save(user);
            return "Password change successful.";
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    public RolesResponse getAllRolesExceptAdmin() {
        List<Role> roles = roleRepository.findAll();
        List<Role> filteredRoles = roles.stream()
                .filter(role -> !role.getAuthority().equals("ADMIN"))
                .toList();

        return new RolesResponse(filteredRoles);
    }
}
