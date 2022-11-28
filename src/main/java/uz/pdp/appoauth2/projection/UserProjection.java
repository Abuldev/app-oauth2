package uz.pdp.appoauth2.projection;

import org.hibernate.service.UnknownUnwrapTypeException;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public interface UserProjection {

    UUID getId();

    String getName();

    String getUsername();

//    AddressProjection getAddress();

    @Value("#{''+(target.name != null ? target.name.charAt(0):'')+target.username.charAt(0)}")
    String getAllName();
}
