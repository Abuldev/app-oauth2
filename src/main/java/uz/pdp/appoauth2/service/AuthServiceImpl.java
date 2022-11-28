package uz.pdp.appoauth2.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.appoauth2.entity.User;
import uz.pdp.appoauth2.exceptions.RestException;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.SignDTO;
import uz.pdp.appoauth2.payload.TokenDTO;
import uz.pdp.appoauth2.payload.UserPrincipal;
import uz.pdp.appoauth2.repository.UserRepository;
import uz.pdp.appoauth2.security.TokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           @Lazy AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserPrincipal.create(userRepository
                .findByPhoneNumber(username)
                .orElseThrow(
                        () -> RestException.restThrow(String.format("%s email not found", username), HttpStatus.UNAUTHORIZED)));

    }

    @Override
    @Transactional
    public ApiResult<TokenDTO> signUp(SignDTO signDTO) {

        if (userRepository.existsByPhoneNumber(signDTO.getPhoneNumber()))
            throw RestException.restThrow(
                    "EMAIL_ALREADY_EXIST",
                    HttpStatus.CONFLICT);


        User user = new User(
                signDTO.getPhoneNumber(),
                passwordEncoder.encode(signDTO.getPassword()));


        userRepository.save(user);
        //todo tokendto qaytar
        return ApiResult.successResponse();
    }


    @Override
    public ApiResult<?> verificationPhoneNumber(String email) {
        User user = userRepository.findByPhoneNumber(email)
                .orElseThrow(() -> RestException.restThrow("EMAIL_NOT_EXIST", HttpStatus.NOT_FOUND));

        if (user.isEnabled()) {
            return ApiResult.successResponse("ALREADY_VERIFIED");
        }

        user.setEnabled(true);
        userRepository.save(user);
        return ApiResult.successResponse("SUCCESSFULLY_VERIFIED");
    }


    @Override
    public ApiResult<TokenDTO> signInForEmployee(SignDTO signInForEmployeeDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInForEmployeeDTO.getPhoneNumber(),
                        signInForEmployeeDTO.getPassword()
                ));

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = tokenProvider.createToken(userPrincipal, true);
        String refreshToken = tokenProvider.createToken(userPrincipal, false);


        TokenDTO tokenDTO = TokenDTO
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ApiResult.successResponse(
                "SUCCESSFULLY_TOKEN_GENERATED",
                tokenDTO);
    }


    @Override
    public ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken) {
        accessToken = accessToken.substring(accessToken.indexOf("Bearer") + 6).trim();
        try {
            Jwts
                    .parser()
                    .setSigningKey("ACCESS_TOKEN_KEY")
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException ex) {
            try {
                String email = Jwts
                        .parser()
                        .setSigningKey("REFRESH_TOKEN_KEY")
                        .parseClaimsJws(refreshToken)
                        .getBody()
                        .getSubject();
                User user = userRepository.findByPhoneNumber(email).orElseThrow(() ->
                        RestException.restThrow("EMAIL_NOT_EXIST", HttpStatus.NOT_FOUND));

                if (!user.isEnabled()
                        || !user.isAccountNonExpired()
                        || !user.isAccountNonLocked()
                        || !user.isCredentialsNonExpired())
                    throw RestException.restThrow("USER_PERMISSION_RESTRICTION", HttpStatus.UNAUTHORIZED);

                String newAccessToken = tokenProvider.createToken(UserPrincipal.create(user), true);
                String newRefreshToken = tokenProvider.createToken(UserPrincipal.create(user), false);
                TokenDTO tokenDTO = TokenDTO.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
                return ApiResult.successResponse(tokenDTO);
            } catch (Exception e) {
                throw RestException.restThrow("REFRESH_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            throw RestException.restThrow("WRONG_ACCESS_TOKEN", HttpStatus.UNAUTHORIZED);
        }

        throw RestException.restThrow("ACCESS_TOKEN_NOT_EXPIRED", HttpStatus.UNAUTHORIZED);
    }


    /**
     * Send Verification Code To PhoneNumber
     */
    private void sendVerificationCodeToPhoneNumber(User user) {
        System.out.println(user);
    }

}
