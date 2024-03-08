package org.authentication.Auth.Payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class AccountDTO {
    @Email
    private String email;

    private String password;
}
