package com.mercatura.backend.service;

import com.mercatura.backend.dto.Statistics.UserStatistics;
import com.mercatura.backend.dto.UUIDResponse;
import com.mercatura.backend.dto.UserResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Image;
import com.mercatura.backend.repository.ImageRepository;
import com.mercatura.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public UserService(UserRepository userRepository, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public List<UserResponse> getAllUsers(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationUser> userPage = userRepository.findAll(pageable);
        return userPage.stream().map(UserResponse::new).collect(Collectors.toList());
    }

    public UserStatistics getAllUsersStatistics() {
        List<ApplicationUser> users = userRepository.findAll();
        return new UserStatistics(users);
    }

    public UserResponse getUserById(UUID id) {
        return new UserResponse(userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("User with id %s not found", id))));
    }

    public UUIDResponse addProfileImage(UUID id, String imageUrl) {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getProfileImage() != null) {
            Image existingImage = user.getProfileImage();
            user.setProfileImage(null);
            userRepository.save(user);
            imageRepository.delete(existingImage);
        }

        Image image = new Image();
        image.setImageUrl(imageUrl);
        image.setUser(user);
        imageRepository.save(image);

        user.setProfileImage(image);
        userRepository.save(user);
        return new UUIDResponse(user.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void deleteUserById(UUID id) {
        boolean exists = userRepository.existsById(id);
        if (exists) {
            userRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("User with id %s not found", id));
        }
    }
}
