package uz.pdp.appoauth2.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;


@Getter
@AllArgsConstructor
public class SignDTO implements Serializable {

    private String phoneNumber;

    private String password;
}
