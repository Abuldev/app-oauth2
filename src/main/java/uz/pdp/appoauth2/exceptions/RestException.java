package uz.pdp.appoauth2.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class RestException extends RuntimeException {

    private final String message;

    private final HttpStatus status;

    private RestException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public static RestException restThrow(String message, HttpStatus status) {
        return new RestException(message, status);
    }
}
