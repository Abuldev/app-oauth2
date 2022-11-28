package uz.pdp.appoauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.UserDTO;
import uz.pdp.appoauth2.payload.UserPrincipal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public ApiResult<UserDTO> getUserByToken(UserPrincipal userPrincipal) {

        UserDTO userDTO = new UserDTO(userPrincipal.getId(), userPrincipal.getName(), userPrincipal.getUsername());

        return ApiResult.successResponse(userDTO);
    }

}
