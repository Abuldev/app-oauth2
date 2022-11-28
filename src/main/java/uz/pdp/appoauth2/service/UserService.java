package uz.pdp.appoauth2.service;

import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.UserDTO;
import uz.pdp.appoauth2.payload.UserPrincipal;

public interface UserService {

    ApiResult<UserDTO> getUserByToken(UserPrincipal userPrincipal);
}
