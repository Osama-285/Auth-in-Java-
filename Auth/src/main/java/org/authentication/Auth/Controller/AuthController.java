package org.authentication.Auth.Controller;

import org.authentication.Auth.Model.Account;
import org.authentication.Auth.Payload.AccountDTO;

import org.authentication.Auth.Util.AccountStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {
    @PostMapping(value = "/signup", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(account.getPassword());
            return ResponseEntity.ok(AccountStatus.ACCOUNT_ADDED.toString());
        } catch (Exception e) {
            log.debug(AccountStatus.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }
    }
}
