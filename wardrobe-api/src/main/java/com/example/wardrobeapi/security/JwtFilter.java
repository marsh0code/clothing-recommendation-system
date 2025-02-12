package com.example.wardrobeapi.security;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
@SecurityScheme(name = "bearer_token", type = SecuritySchemeType.HTTP, scheme = "bearer",
        description = "Enter the token given after successful POST /auth/sign-in",
        bearerFormat = "JWT")
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        var maybeToken = getTokenFromRequest(request);
        if (maybeToken.isPresent()) {
            var token = maybeToken.get();
            if (jwtProvider.toDecodedJWT(token).isPresent()) {
                var username = jwtProvider.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        var bearer = request.getHeader("Authorization");
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            return Optional.of(bearer.substring(7));
        }
        return Optional.empty();
    }
}
