package org.authentication.Auth.Payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SignupResponseDTO {
    private String status;
    private String token;
    private String message;
}
