package com.phungvanlong.booking_hotel.security;

import com.phungvanlong.booking_hotel.entity.Role;
import com.phungvanlong.booking_hotel.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // 1. Check if token has been revoked / blacklisted
                if (tokenBlacklistService.isAccessTokenBlacklisted(jwt)) {
                    logger.warn("Token is blacklisted: " + jwt);
                    filterChain.doFilter(request, response);
                    return;
                }

                // 2. Validate and extract claims in a single pass (Stateless - Zero DB hit)
                Claims claims = null;
                try {
                    claims = tokenProvider.extractClaims(jwt);
                } catch (Exception ex) {
                    // Invalid/Expired token - already logged by JwtTokenProvider
                }

                if (claims != null) {
                    String email = claims.getSubject();
                    Object userIdObj = claims.get("userId");
                    Long userId = (userIdObj instanceof Number num) ? num.longValue() : null;
                    String roleName = claims.get("role", String.class);
                    Role role = null;
                    if (roleName != null) {
                        try {
                            role = Role.valueOf(roleName);
                        } catch (Exception ignored) {}
                    }

                    // Create UserPrincipal directly from verified claims
                    UserPrincipal userPrincipal = UserPrincipal.create(userId, email, role);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userPrincipal, null, userPrincipal.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
