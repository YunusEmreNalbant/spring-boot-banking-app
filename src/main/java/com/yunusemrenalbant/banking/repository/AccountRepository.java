package com.yunusemrenalbant.banking.repository;

import com.yunusemrenalbant.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
