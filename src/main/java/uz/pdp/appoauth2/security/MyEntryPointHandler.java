package uz.pdp.appoauth2.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import uz.pdp.appoauth2.payload.ApiResult;
import uz.pdp.appoauth2.payload.ErrorData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Component
@Slf4j
public class MyEntryPointHandler implements AuthenticationEntryPoint {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ApiResult<List<ErrorData>> apiResult = ApiResult.failResponse(
                "Full authentication is required to access this resource",
                HttpStatus.UNAUTHORIZED.value());
        log.error("Full authentication is required to access this resource: {}", request.getRequestURI());
        try {
            String string = objectMapper.writeValueAsString(apiResult);
            response.getWriter().write(string);
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
