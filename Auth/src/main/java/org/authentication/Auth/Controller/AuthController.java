package org.authentication.Auth.Controller;

import org.authentication.Auth.Payload.AccountDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    @PostMapping(value = "/signup", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
