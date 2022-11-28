package uz.pdp.appoauth2.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.UserDTO;
import uz.pdp.appoauth2.payload.UserPrincipal;
import uz.pdp.appoauth2.security.CurrentUser;

@RequestMapping(path = "/api/user")
public interface UserController {
    String USER_ME_PATH = "/me";


    @GetMapping(value = USER_ME_PATH)
    ApiResult<UserDTO> getUserByToken(@CurrentUser UserPrincipal userPrincipal);

}
