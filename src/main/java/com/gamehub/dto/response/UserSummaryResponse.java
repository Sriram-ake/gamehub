package com.gamehub.dto.response;

import com.gamehub.model.User;

public class UserSummaryResponse {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String role;
    private int totalXp;
    private int level;

    public UserSummaryResponse() {
    }

    public UserSummaryResponse(Long id, String username, String email, String displayName,
                               String avatarUrl, String role, int totalXp, int level) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.totalXp = totalXp;
        this.level = level;
    }

    public static UserSummaryResponse fromEntity(User user) {
        int level = (int) Math.floor(Math.sqrt(user.getTotalXp() / 50.0)) + 1;
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
                user.getAvatarUrl() != null ? user.getAvatarUrl() : "/assets/images/default-avatar.svg",
                user.getRole() != null ? user.getRole().name() : "PLAYER",
                user.getTotalXp(),
                level
        );
    }

    public static UserSummaryResponseBuilder builder() {
        return new UserSummaryResponseBuilder();
    }

    public static class UserSummaryResponseBuilder {
        private Long id;
        private String username;
        private String email;
        private String displayName;
        private String avatarUrl;
        private String role;
        private int totalXp;
        private int level;

        UserSummaryResponseBuilder() {
        }

        public UserSummaryResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSummaryResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserSummaryResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserSummaryResponseBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public UserSummaryResponseBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserSummaryResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserSummaryResponseBuilder totalXp(int totalXp) {
            this.totalXp = totalXp;
            return this;
        }

        public UserSummaryResponseBuilder level(int level) {
            this.level = level;
            return this;
        }

        public UserSummaryResponse build() {
            return new UserSummaryResponse(id, username, email, displayName, avatarUrl, role, totalXp, level);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
