package com.example.demo.configuration;

import com.example.demo.entity.Person;
import com.example.demo.interfaces.account_work.JwtInterface;
import com.example.demo.repository.PersonRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtInterface jwtService;
    private final PersonRepository personRepository;
    private final AuthenticationEntryPoint entryPoint;

    JwtAuthFilter(JwtInterface jwtService,
                  PersonRepository personRepository,
                  AuthenticationEntryPoint entryPoint) {
        this.jwtService = jwtService;
        this.personRepository = personRepository;
        this.entryPoint = entryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        return uri.equals("/swagger-ui.html")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(token)) {
                entryPoint.commence(request, response,
                        new BadCredentialsException("Invalid JWT"));
                return;
            }

            String username = jwtService.extractUsername(token);
            if (username == null || username.isBlank()) {
                entryPoint.commence(request, response,
                        new BadCredentialsException("No subject in JWT"));
                return;
            }

            Person person = personRepository.findByUsername(username);
            if (person == null || !person.isActive()) {
                entryPoint.commence(request, response,
                        new CredentialsExpiredException("User inactive or missing"));
                return;
            }

            var authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + person.getRole().name())
            );

            var authToken = new UsernamePasswordAuthenticationToken(
                    person, null, authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            chain.doFilter(request, response);

        } catch (io.jsonwebtoken.JwtException e) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response,
                    new BadCredentialsException("Malformed or tampered JWT", e));
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response,
                    new AuthenticationServiceException("JWT auth error", e));
        }
    }
}
