package uz.pdp.appoauth2.payload;


import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenDTO {

    private String accessToken;

    private String refreshToken;

    private final String tokenType = "Bearer ";
}
