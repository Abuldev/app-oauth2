package uz.pdp.appoauth2.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.pdp.appoauth2.exceptions.OAuth2AuthenticationProcessingException;
import uz.pdp.appoauth2.exceptions.RestException;
import uz.pdp.appoauth2.payload.UserPrincipal;
import uz.pdp.appoauth2.utils.CookieUtils;
import uz.pdp.appoauth2.utils.RestConstants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static uz.pdp.appoauth2.security.HttpCookieOAuth2AuthorizationRequestRepository.*;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy()
                .sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> optionalSignRedirectUri = CookieUtils
                .getCookie(request, REDIRECT_URI_SIGN_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        Optional<String> optionalConnectRedirectUri = CookieUtils
                .getCookie(request, REDIRECT_URI_CONNECT_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

//        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get()))
//            throw new OAuth2AuthenticationProcessingException();

        Optional<String> optionalConnectToken = CookieUtils
                .getCookie(request, TOKEN_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        String targetUrl = null;
        String token = null;
        if (optionalSignRedirectUri.isPresent())
            targetUrl = optionalSignRedirectUri.orElse(getDefaultTargetUrl());
        else if (optionalConnectRedirectUri.isPresent()) {
            targetUrl = optionalConnectRedirectUri.get();
//            Optional<String> optionalConnectToken = CookieUtils
//                    .getCookie(request, TOKEN_PARAM_COOKIE_NAME)
//                    .map(Cookie::getValue);
            if (optionalConnectToken.isEmpty())
                throw new OAuth2AuthenticationProcessingException();
            token = optionalConnectToken.get();
        }


        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = null;
        String refreshToken = null;
        if (Objects.nonNull(userPrincipal.getId())) {
            accessToken = tokenProvider.createToken(userPrincipal, true);
            refreshToken = tokenProvider.createToken(userPrincipal, false);
        }
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam(RestConstants.ACCESS_TOKEN, RestConstants.TOKEN_TYPE + accessToken)
                .queryParam(RestConstants.REFRESH_TOKEN, refreshToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//
//        return appProperties.getOauth2().getAuthorizedRedirectUris()
//                .stream()
//                .anyMatch(authorizedRedirectUri -> {
//                    // Only validate host and port. Let the clients use different paths if they want to
//                    URI authorizedURI = URI.create(authorizedRedirectUri);
//                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
//                        return true;
//                    }
//                    return false;
//                });

        return true;
    }
}