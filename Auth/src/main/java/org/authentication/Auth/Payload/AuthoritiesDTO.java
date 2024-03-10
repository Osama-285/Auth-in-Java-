package org.authentication.Auth.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthoritiesDTO {

    @NotBlank
    @Schema(description = "Authorities", example = "USER", required = true)
    private String authorities;
}
