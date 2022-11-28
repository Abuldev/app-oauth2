package uz.pdp.appoauth2.security;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.appoauth2.entity.User;
import uz.pdp.appoauth2.payload.UserPrincipal;
import uz.pdp.appoauth2.repository.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final String AUTHENTICATION_HEADER = "Authorization";

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        setSecurityContext(request);
        filterChain.doFilter(request, response);
    }

    private void setSecurityContext(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHENTICATION_HEADER);
        if (Objects.nonNull(authorization)
                && authorization.startsWith("Bearer")
                && (tokenProvider.validateToken(authorization = authorization.substring(6).trim()))) {

            UUID id = tokenProvider.getUserIdFromToken(authorization);
            if (Objects.nonNull(id)) {
                Optional<User> optionalUser = userRepository.findById(id);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    UserPrincipal userPrincipal = UserPrincipal.create(user);

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    new UsernamePasswordAuthenticationToken(
                                            userPrincipal,
                                            null,
                                            new ArrayList<>()
                                    ));
                }
            }
        }
    }
}
