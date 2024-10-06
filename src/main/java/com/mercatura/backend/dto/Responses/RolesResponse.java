package com.mercatura.backend.dto.Responses;

import com.mercatura.backend.entity.Role;
import lombok.Data;
import java.util.List;

@Data
public class RolesResponse {
    private List<String> roles;

    public RolesResponse(List<Role> roles) {
        this.roles = roles.stream().map(Role::getAuthority)
                .toList();
    }
}
