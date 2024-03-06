package org.authentication.Auth.Repository;

import java.util.Optional;

import org.authentication.Auth.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

}
