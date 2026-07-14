package com.alertaid.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alertaid.security.CustomOidcUserService;
import com.alertaid.security.JwtAuthenticationEntryPoint;
import com.alertaid.security.JwtAuthenticationFilter;
import com.alertaid.security.RoleBasedAuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // NOTE: page/asset access is intentionally NOT gated by a filename
    // allowlist anymore. This app's real access control lives at the
    // API layer (JWT filter + @PreAuthorize on controller methods) —
    // the HTML/CSS/JS is a public SPA + legacy static pages that any
    // visitor can load; what they can *do* is enforced by the API.
    // A hardcoded list of frontend filenames doesn't scale (every new
    // file — a Vite-hashed bundle, a renamed page — has to be added by
    // hand or it silently 401s). See the broad GET permitAll below.

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrations;
    private final CustomOidcUserService customOidcUserService;
    private final RoleBasedAuthenticationSuccessHandler successHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint unauthorizedHandler,
                          ObjectProvider<ClientRegistrationRepository> clientRegistrations,
                          CustomOidcUserService customOidcUserService,
                          RoleBasedAuthenticationSuccessHandler successHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.unauthorizedHandler = unauthorizedHandler;
        this.clientRegistrations = clientRegistrations;
        this.customOidcUserService = customOidcUserService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/oauth2/**", "/logout").permitAll()

                // ---- API: granular, unchanged in spirit ----
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/seekforhelp").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reports/**", "/api/stream/**").permitAll()
                .requestMatchers("/api/**").authenticated() // everything else under /api needs a valid JWT;
                                                              // role checks still enforced by @PreAuthorize

                // ---- Frontend: all pages + assets are public, from wherever
                // spring.web.resources.static-locations points (the frontend
                // dir). No per-file allowlist to maintain. ----
                .requestMatchers(HttpMethod.GET, "/**").permitAll()

                .anyRequest().authenticated()
            );

        // Enable OAuth2 login if client is configured
        if (clientRegistrations.getIfAvailable() != null) {
            http.oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(ui -> ui.oidcUserService(customOidcUserService))
                .successHandler(successHandler)
            );
        }

        http.logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout").permitAll()
            .deleteCookies("JSESSIONID")
            .clearAuthentication(true)
            .invalidateHttpSession(true)
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
        
