package uz.pdp.appoauth2.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private UUID id;

    private String name;

    private String username;


}
