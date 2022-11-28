
package uz.pdp.appoauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import uz.pdp.appoauth2.entity.AuthProvider;
import uz.pdp.appoauth2.entity.User;
import uz.pdp.appoauth2.exceptions.OAuth2AuthenticationProcessingException;
import uz.pdp.appoauth2.exceptions.RestException;
import uz.pdp.appoauth2.payload.OAuth2UserInfo;
import uz.pdp.appoauth2.payload.OAuth2UserInfoFactory;
import uz.pdp.appoauth2.payload.UserPrincipal;
import uz.pdp.appoauth2.repository.UserRepository;
import uz.pdp.appoauth2.utils.CookieUtils;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (Objects.isNull(oAuth2UserInfo.getUsername()) || oAuth2UserInfo.getUsername().isBlank()) {
            throw new OAuth2AuthenticationProcessingException();
        }

        Optional<User> userOptional = Optional.empty();

        AuthProvider authProvider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId());

        if (AuthProvider.google.equals(authProvider))
            userOptional = userRepository.findByGoogleUsername(oAuth2UserInfo.getUsername());
//            userOptional = userRepository.findByPhoneNumber("+998996791136");
        else if (Objects.equals(
                AuthProvider.github,
                authProvider)) {

        }

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();

//            if (!user.getProvider().equals(AuthProvider.valueOf(
//                    oAuth2UserRequest.getClientRegistration().getRegistrationId())))
//                throw new OAuth2AuthenticationProcessingException();

            user = updateExistingUser(user, oAuth2UserInfo);
        } else
            return UserPrincipal.create(new User());
//            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);


        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        throw RestException.restThrow("OKa avval tel bilan register qiing", HttpStatus.BAD_REQUEST);
//        User user = new User();
//
//        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
//        user.setProviderId(oAuth2UserInfo.getId());
//        user.setName(oAuth2UserInfo.getName());
//        user.setUsername(oAuth2UserInfo.getUsername());
//        user.setImageUrl(oAuth2UserInfo.getImageUrl());
//        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

}