package org.authentication.Auth.Payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;;

@Getter
@Setter
public class LoginDTO {
    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;
}
