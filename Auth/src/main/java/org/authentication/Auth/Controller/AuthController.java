package org.authentication.Auth.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.authentication.Auth.Model.Account;
import org.authentication.Auth.Payload.AccountDTO;
import org.authentication.Auth.Payload.AccountViewDTO;
import org.authentication.Auth.Payload.AuthoritiesDTO;
import org.authentication.Auth.Payload.LoginDTO;
import org.authentication.Auth.Payload.PasswordDTO;
import org.authentication.Auth.Payload.ProfileDTO;
import org.authentication.Auth.Payload.SignupResponseDTO;
import org.authentication.Auth.Payload.TokenDTO;
import org.authentication.Auth.Services.AccountService;
import org.authentication.Auth.Services.TokenService;
import org.authentication.Auth.Util.AccountStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> authToken(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
        } catch (Exception e) {
            log.debug(AccountStatus.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }

    }

    // @PostMapping(value = "/signup", produces = "application/json")
    // @ResponseStatus(HttpStatus.CREATED)
    // public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO
    // accountDTO) {
    // try {
    // Account account = new Account();
    // account.setEmail(accountDTO.getEmail());
    // account.setPassword(accountDTO.getPassword());
    // account.setAuthorities(accountDTO.getAuthorities());
    // System.out.println("USERINPUT" + account + accountDTO);
    // accountService.save(account);
    // return ResponseEntity.ok(AccountStatus.ACCOUNT_ADDED.toString());
    // } catch (Exception e) {
    // log.debug(AccountStatus.ADD_ACCOUNT_ERROR.toString() + ": " +
    // e.getMessage());
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    // }
    // }

    @PostMapping(value = "/signup", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SignupResponseDTO> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
            account.setAuthorities(accountDTO.getAuthorities());
            System.out.println("USERINPUT" + account + accountDTO);
            accountService.save(account);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(accountDTO.getEmail(), accountDTO.getPassword()));

            accountService.save(account);

            String token = tokenService.generateToken(authentication);
            SignupResponseDTO responseDTO = new SignupResponseDTO(AccountStatus.ACCOUNT_ADDED.toString(), token, null);

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.debug(AccountStatus.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            String errorMessage;
            HttpStatus status = HttpStatus.BAD_REQUEST;

            // Check if the exception is due to an existing user
            if (e instanceof DataIntegrityViolationException) {
                errorMessage = "User with this email already exists";
            } else {
                errorMessage = e.getMessage();
            }

            SignupResponseDTO responseDTO = new SignupResponseDTO(status.toString(), null, errorMessage);
            return ResponseEntity.status(status).body(responseDTO);
        }

    }

    @GetMapping(value = "/user", produces = "application/json")
    public List<AccountViewDTO> Users() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account account : accountService.findall()) {
            accounts.add(new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities()));
        }
        return accounts;
    }

    @PutMapping(value = "/users/{user_id}/update", produces = "application/json")
    public ResponseEntity<AccountViewDTO> updateUser(@Valid @RequestBody AuthoritiesDTO authoritiesDTO,
            @PathVariable long user_id) {
        Optional<Account> optionalAccount = accountService.findByID(user_id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setAuthorities(authoritiesDTO.getAuthorities());
            accountService.save(account);
            AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(),
                    account.getAuthorities());
            return ResponseEntity.ok(accountViewDTO);
        }
        return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/users/updatePassword", produces = "application/json")
    public AccountViewDTO update_password(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {
        String email = authentication.getName();
        System.out.println("NAME  EMAIL " + email);
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("USER R R R " + user);
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        account.setPassword(passwordDTO.getPassword());
        accountService.save(account);
        AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(),
                account.getAuthorities());
        return accountViewDTO;
    }

    @GetMapping(value = "/profile", produces = "application/json")
    public ProfileDTO profile(Authentication authentication) {
        String email = authentication.getName();
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("USER R R R " + user);
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getAuthorities());
        return profileDTO;
    }
}
