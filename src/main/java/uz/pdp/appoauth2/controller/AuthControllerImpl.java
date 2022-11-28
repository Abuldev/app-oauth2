package uz.pdp.appoauth2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.SignDTO;
import uz.pdp.appoauth2.payload.TokenDTO;
import uz.pdp.appoauth2.service.AuthService;


@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;

    public ApiResult<TokenDTO> signUp(SignDTO signDTO) {
        return authService.signUp(signDTO);
    }


    @Override
    public ApiResult<TokenDTO> signInForEmployee(SignDTO signInForEmployeeDTO) {
        return authService.signInForEmployee(signInForEmployeeDTO);
    }

    @Override
    public ApiResult<TokenDTO> refreshToken(String accessToken, String refreshToken) {
        return authService.refreshToken(accessToken, refreshToken);
    }


}
