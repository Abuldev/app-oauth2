package uz.pdp.appoauth2.controller;

import org.springframework.web.bind.annotation.*;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.SignDTO;
import uz.pdp.appoauth2.payload.TokenDTO;


@RequestMapping(path = "/api/auth")
public interface AuthController {
    String SIGN_IN_FOR_EMPLOYEE_PATH = "/sign-in";
    String SIGN_UP_PATH = "/sign-up";
    String REFRESH_TOKEN_PATH = "/refresh-token";


    @PostMapping(value = SIGN_UP_PATH)
    ApiResult<TokenDTO> signUp(@RequestBody SignDTO signDTO);


    @PostMapping(value = SIGN_IN_FOR_EMPLOYEE_PATH)
    ApiResult<TokenDTO> signInForEmployee(@RequestBody SignDTO signDTO);


    @GetMapping(value = REFRESH_TOKEN_PATH)
    ApiResult<TokenDTO> refreshToken(@RequestHeader(value = "Authorization") String accessToken,
                                     @RequestHeader(value = "RefreshToken") String refreshToken);

}
