package com.alertaid.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alertaid.model.Role;
import com.alertaid.model.User;
import com.alertaid.security.JwtTokenProvider;
import com.alertaid.service.UserService;

@RestController
@RequestMapping("/api/auth/oauth")
public class OAuthController {

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;
    @Value("${GOOGLE_CLIENT_SECRET:}")
    private String googleClientSecret;
    // Default to the path used by the frontend without context-path
    @Value("${app.oauth.google.callback-uri:http://localhost:8080/api/auth/oauth/google/callback}")
    private String googleCallbackUri;

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom secureRandom = new SecureRandom();

    public OAuthController(UserService userService, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // Step 1: Redirect user to Google consent
    @GetMapping("/google")
    public ResponseEntity<Void> startGoogle(@RequestParam("redirect_uri") String redirectUri,
                                            @RequestParam(value = "state", required = false) String state,
                                            @RequestParam(value = "role", required = false) String role) {
        if (isBlank(googleClientId) || isBlank(googleClientSecret)) {
            return ResponseEntity.status(302).location(URI.create("/login.html?oauth=missing_config")).build();
        }

        // Build state object if not provided, or enhance existing state with redirect_uri
        String finalState = state;
        if (isBlank(state)) {
            try {
                Map<String, Object> stateMap = new java.util.HashMap<>();
                stateMap.put("ts", System.currentTimeMillis());
                stateMap.put("role", role != null ? role : "CITIZEN");
                stateMap.put("redirect_uri", redirectUri);
                stateMap.put("mode", "signin"); // default mode
                String jsonState = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(stateMap);
                finalState = Base64.getEncoder().encodeToString(jsonState.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                // Fallback to simple state
                finalState = Base64.getEncoder().encodeToString(("ts=" + System.currentTimeMillis() + "&role=" + (role != null ? role : "CITIZEN")).getBytes(StandardCharsets.UTF_8));
            }
        } else {
            // If state is provided, try to enhance it with redirect_uri
            try {
                String decodedState = new String(Base64.getDecoder().decode(state), StandardCharsets.UTF_8);
                @SuppressWarnings("unchecked")
                Map<String, Object> stateMap = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(decodedState, Map.class);
                if (stateMap != null) {
                    stateMap.put("redirect_uri", redirectUri);
                    String jsonState = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(stateMap);
                    finalState = Base64.getEncoder().encodeToString(jsonState.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                // Keep original state if parsing fails
            }
        }

        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?response_type=code" +
                "&client_id=" + url(googleClientId) +
                "&redirect_uri=" + url(googleCallbackUri) +
                "&scope=" + url("openid email profile") +
                "&state=" + url(finalState) +
                "&access_type=online" +
                "&prompt=consent";
        return ResponseEntity.status(302).location(URI.create(authUrl)).build();
    }

    // Step 2: Exchange code and redirect back to frontend login with result
    @GetMapping({"/google/callback", "/login/oauth2/code/google"})
    public ResponseEntity<Void> googleCallback(@RequestParam("code") String code,
                                               @RequestParam(value = "state", required = false) String state,
                                               @RequestParam(value = "error", required = false) String error) {
        try {
            // Handle OAuth error from Google
            if (error != null) {
                return ResponseEntity.status(302).location(URI.create("/login.html?oauth=error&reason=" + url(error))).build();
            }

            if (isBlank(googleClientId) || isBlank(googleClientSecret)) {
                return ResponseEntity.status(302).location(URI.create("/login.html?oauth=missing_config")).build();
            }

            // Parse state to extract role and redirect_uri
            String roleStr = "CITIZEN";
            String frontendRedirectUri = "/login.html";
            if (!isBlank(state)) {
                try {
                    String decodedState = new String(Base64.getDecoder().decode(state), StandardCharsets.UTF_8);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> stateMap = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(decodedState, Map.class);
                    if (stateMap != null) {
                        Object roleObj = stateMap.get("role");
                        if (roleObj != null) roleStr = roleObj.toString().toUpperCase();
                        Object redirectObj = stateMap.get("redirect_uri");
                        if (redirectObj != null) frontendRedirectUri = redirectObj.toString();
                    }
                } catch (Exception e) {
                    // If state parsing fails, use defaults
                }
            }

            RestTemplate rt = new RestTemplate();
            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("code", code);
            form.add("client_id", googleClientId);
            form.add("client_secret", googleClientSecret);
            form.add("redirect_uri", googleCallbackUri);
            form.add("grant_type", "authorization_code");

            var tokenHeaders = new HttpHeaders();
            tokenHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            var tokenEntity = new org.springframework.http.HttpEntity<>(form, tokenHeaders);
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenRes = rt.postForObject(tokenEndpoint, tokenEntity, Map.class);
            String accessToken = tokenRes != null ? (String) tokenRes.get("access_token") : null;

            if (accessToken == null) {
                return ResponseEntity.status(302).location(URI.create("/login.html?oauth=error&reason=token_failed")).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            var entity = new org.springframework.http.HttpEntity<>(headers);
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = rt.postForObject("https://www.googleapis.com/oauth2/v3/userinfo", entity, Map.class);
            String email = userInfo != null ? (String) userInfo.get("email") : null;

            if (email == null) {
                return ResponseEntity.status(302).location(URI.create("/login.html?oauth=error&reason=no_email")).build();
            }

            // Parse role from state or use default
            Role requestedRole = Role.CITIZEN;
            try {
                requestedRole = Role.valueOf(roleStr);
            } catch (IllegalArgumentException e) {
                // Invalid role, use default
            }
            // Make final copy for lambda expression
            final Role finalRequestedRole = requestedRole;

            Optional<User> existing = userService.getUserByEmail(email);
            User user = existing.orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setName((String) userInfo.getOrDefault("name", email));
                u.setRole(finalRequestedRole);
                // Generate a secure random password for OAuth users (they'll never use it)
                String randomPassword = generateSecureRandomPassword();
                u.setPassword(passwordEncoder.encode(randomPassword));
                return userService.saveUser(u);
            });

            // For existing users, preserve their original role
            // New users already have the requestedRole set during creation

            String appJwt = tokenProvider.generateToken(user.getEmail());

            // Use query params instead of hash fragments for better popup compatibility
            String redirect = frontendRedirectUri + "?oauth=success&token=" + url(appJwt) +
                    "&email=" + url(user.getEmail()) + "&role=" + url(user.getRole().name());
            return ResponseEntity.status(302).location(URI.create(redirect)).build();
        } catch (Exception e) {
            return ResponseEntity.status(302).location(URI.create("/login.html?oauth=error&reason=exception")).build();
        }
    }

    /**
     * Generate a secure random password for OAuth users (they'll authenticate via OAuth, not password)
     */
    private String generateSecureRandomPassword() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private static String url(String v) {
        try { return java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8); }
        catch (Exception e) { return v; }
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
