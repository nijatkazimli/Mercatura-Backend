package com.mercatura.backend.dto.RequestBody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeBody {
    private String username;
    private String currentPassword;
    private String newPassword;
}
