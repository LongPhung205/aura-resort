package com.phungvanlong.booking_hotel.config;

import com.phungvanlong.booking_hotel.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final com.phungvanlong.booking_hotel.security.JwtAuthenticationEntryPoint unauthorizedHandler;
    private final com.phungvanlong.booking_hotel.security.JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/staff/roster", "/admin/staff/swap-requests/**", "/admin/staff/weekly-registrations/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_STAFF", "ROLE_ACCOUNTANT", "ROLE_HOUSEKEEPING", "ROLE_BUTLER")
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_STAFF", "ROLE_ACCOUNTANT")
                        .requestMatchers("/housekeeping/**").hasAnyAuthority("ROLE_HOUSEKEEPING", "ROLE_ADMIN", "ROLE_STAFF")
                        .requestMatchers("/room-types/**", "/rooms/**", "/villa-types/**", "/villas/**", "/banners/**", "/zones/**", "/public/**", "/promotions/**", "/reviews/**", "/extra-services/**", "/combo-packages/**").permitAll()
                        // AI Chat — public (auth optional, AI dùng để create booking nếu đã đăng nhập)
                        .requestMatchers("/ai/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // MoMo redirect - phải public vì MoMo gọi tới không có JWT
                        .requestMatchers("/payments/momo-return", "/payments/momo-ipn").permitAll()
                        // Bất kỳ user đã đăng nhập (bất kỳ role) cũng có thể đặt phòng và thanh toán
                        .requestMatchers("/bookings/**", "/payments/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:4200",
            "http://localhost:*",
            "https://*.yourdomain.com",
            "*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
