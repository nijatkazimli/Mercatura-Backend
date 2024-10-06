package com.mercatura.backend.dto.Statistics;

import com.mercatura.backend.entity.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatistics {
    private Integer totalCount;
    private Integer adminCount;
    private Integer merchandiserCount;
    private Integer regularUserCount;

    public UserStatistics(List<ApplicationUser> users) {
        this.totalCount = users.size();

        this.adminCount = (int) users.stream()
                .filter(user -> user.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ADMIN")))
                .count();

        this.merchandiserCount = (int) users.stream()
                .filter(user -> user.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("MERCHANDISER")))
                .count();

        this.regularUserCount = totalCount - merchandiserCount - adminCount;
    }
}
