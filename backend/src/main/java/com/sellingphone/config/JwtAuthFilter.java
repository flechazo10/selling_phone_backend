package com.sellingphone.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter chặn mọi request HTTP và kiểm tra JWT từ header Authorization.
 *
 * Luồng xử lý:
 *  1. Đọc header "Authorization: Bearer <token>"
 *  2. Trích xuất username từ token
 *  3. Load UserDetails từ DB
 *  4. Validate token (chữ ký + hết hạn + đúng user)
 *  5. Nếu hợp lệ → set Authentication vào SecurityContext
 *  6. Tiếp tục filter chain (dù token có hợp lệ hay không)
 *
 * Extends {@link OncePerRequestFilter} đảm bảo filter chỉ chạy 1 lần / request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService           jwtService;
    private final UserDetailsService   userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Bỏ qua nếu không có JWT hoặc format sai
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt      = authHeader.substring(7);
        final String username;

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Token lỗi (malformed, expired) → tiếp tục mà không set auth
            log.debug("[JwtAuthFilter] Token không đọc được: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Chỉ set Authentication nếu chưa có (tránh set lại trong cùng request)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("[JwtAuthFilter] Authenticated: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
