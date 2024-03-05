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

    @Size(min = 6, max = 20)
    private String password;
}
