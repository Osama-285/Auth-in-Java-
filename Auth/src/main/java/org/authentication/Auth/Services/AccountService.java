package org.authentication.Auth.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.authentication.Auth.Model.Account;
import org.authentication.Auth.Repository.AccountRepo;
import org.authentication.Auth.Util.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account save(Account account) {
        System.out.println("PASSWORD" + account);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getAuthorities() == null) {
            account.setAuthorities(Authority.USER.toString());
        }
        return accountRepo.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalUser = accountRepo.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        Account account = optionalUser.get();

        List<GrantedAuthority> grantedAuthoriy = new ArrayList<>();
        grantedAuthoriy.add(new SimpleGrantedAuthority(account.getAuthorities()));
        return new User(account.getEmail(), account.getPassword(), grantedAuthoriy);
    }
}
