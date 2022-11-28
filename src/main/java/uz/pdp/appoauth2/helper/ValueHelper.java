package uz.pdp.appoauth2.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.appoauth2.entity.User;
import uz.pdp.appoauth2.projection.UserProjection;

import java.util.Map;

@Component(value = "valueHelper")
public class ValueHelper {

    public String getAllName(Object o) {
        System.out.println(o);
        Map<String, Object> map = (Map<String, Object>) o;
        System.out.println(map);
        return map.get("username") + " " + map.get("name");
//        return user.getName()+" "+user.getUsername();
    }
}
