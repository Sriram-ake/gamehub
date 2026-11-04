package com.gamehub.controller;

import com.gamehub.dto.response.UserSummaryResponse;
import com.gamehub.security.UserPrincipal;
import com.gamehub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/users", "/api/v1/users"})
@Tag(name = "Users", description = "Endpoints for user profile and stats")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get own profile", description = "Returns player profile details of currently authenticated user")
    public ResponseEntity<UserSummaryResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getUserById(principal.getId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update own profile", description = "Update display name or avatar URL")
    public ResponseEntity<UserSummaryResponse> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> payload) {
        String displayName = payload.get("displayName");
        String avatarUrl = payload.get("avatarUrl");
        return ResponseEntity.ok(userService.updateProfile(principal.getId(), displayName, avatarUrl));
    }

    @GetMapping("/me/stats")
    @Operation(summary = "Get player statistics", description = "Returns total play sessions, XP, level, and distinct games played")
    public ResponseEntity<Map<String, Object>> getMyStats(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getUserStats(principal.getId()));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get public player profile by username")
    public ResponseEntity<UserSummaryResponse> getPublicProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}
