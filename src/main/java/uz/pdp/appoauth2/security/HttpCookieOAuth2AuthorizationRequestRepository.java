package uz.pdp.appoauth2.security;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import uz.pdp.appoauth2.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_SIGN_PARAM_COOKIE_NAME = "redirect_uri_sign";
    public static final String REDIRECT_URI_CONNECT_PARAM_COOKIE_NAME = "redirect_uri_connect";
    public static final String TOKEN_PARAM_COOKIE_NAME = "token";
    private static final int cookieExpireSeconds = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_SIGN_PARAM_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_CONNECT_PARAM_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, TOKEN_PARAM_COOKIE_NAME);
            return;
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_SIGN_PARAM_COOKIE_NAME);
        String redirectUriAfterConnect = request.getParameter(REDIRECT_URI_CONNECT_PARAM_COOKIE_NAME);
        String token = request.getParameter(TOKEN_PARAM_COOKIE_NAME);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response,
                    REDIRECT_URI_SIGN_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    cookieExpireSeconds);
            CookieUtils.addCookie(response,
                    REDIRECT_URI_CONNECT_PARAM_COOKIE_NAME,
                    redirectUriAfterConnect,
                    cookieExpireSeconds);
            CookieUtils.addCookie(response,
                    TOKEN_PARAM_COOKIE_NAME,
                    "sdsadsadsadsadsadasdsadasdas",
                    cookieExpireSeconds);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_SIGN_PARAM_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_CONNECT_PARAM_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, TOKEN_PARAM_COOKIE_NAME);
    }
}