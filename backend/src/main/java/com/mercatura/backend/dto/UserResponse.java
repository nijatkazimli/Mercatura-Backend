package com.mercatura.backend.dto;

import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String userName;
    private String profileImage;

    public UserResponse(ApplicationUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUsername();
        Image profileImage = user.getProfileImage();
        if (profileImage != null) {
            this.profileImage = profileImage.getImageUrl();
        }
    }

}
