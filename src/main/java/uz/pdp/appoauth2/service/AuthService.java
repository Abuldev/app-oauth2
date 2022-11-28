package uz.pdp.appoauth2.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.SignDTO;
import uz.pdp.appoauth2.payload.TokenDTO;


public interface AuthService extends UserDetailsService {
    ApiResult<TokenDTO> signUp(SignDTO signDTO);

    ApiResult<?> verificationPhoneNumber(String phoneNumber);

    ApiResult<TokenDTO> signInForEmployee(SignDTO signDTO);

    ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken);

}
