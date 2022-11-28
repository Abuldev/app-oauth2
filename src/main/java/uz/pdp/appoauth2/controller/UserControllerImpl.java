package uz.pdp.appoauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.UserDTO;
import uz.pdp.appoauth2.payload.UserPrincipal;
import uz.pdp.appoauth2.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;


    @Override
    public ApiResult<UserDTO> getUserByToken(UserPrincipal userPrincipal) {
        return userService.getUserByToken(userPrincipal);
    }

}
