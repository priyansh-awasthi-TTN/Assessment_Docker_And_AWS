package com.example.ecommerceproject.filter;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.ecommerceproject.service.MessageService;
import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.service.impl.CustomUserDetailsServiceImpl;
import com.example.ecommerceproject.util.JwtUtil;
import com.example.ecommerceproject.util.MessageKeys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final JwtUtil jwtUtil;
    final CustomUserDetailsServiceImpl userDetailsService;
    final UserSessionService userSessionService;
    final MessageService messageService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                if (!jwtUtil.isTokenValid(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String jti = jwtUtil.extractJti(token);
                if (!userSessionService.isAccessTokenValid(jti)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                email = jwtUtil.extractEmail(token);

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                writeJwtErrorResponse(response, MessageKeys.JWT_TOKEN_EXPIRED);
                return;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                writeJwtErrorResponse(response, MessageKeys.JWT_TOKEN_MALFORMED);
                return;
            } catch (io.jsonwebtoken.JwtException e) {
                writeJwtErrorResponse(response, MessageKeys.JWT_TOKEN_INVALID);
                return;
            } catch (Exception e) {
                writeJwtErrorResponse(response, MessageKeys.JWT_TOKEN_INVALID);
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } catch (Exception e) {

            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeJwtErrorResponse(HttpServletResponse response, String messageKey) throws IOException {
        String message = messageService.get(messageKey);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
            "{\"timestamp\":\"%s\",\"message\":\"%s\",\"status\":%d}",
            java.time.LocalDateTime.now().toString(),
            message,
            HttpServletResponse.SC_UNAUTHORIZED
        );

        response.getWriter().write(jsonResponse);
    }
}
