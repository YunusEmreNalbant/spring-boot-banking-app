package com.yunusemrenalbant.banking.mapper;

import com.yunusemrenalbant.banking.dto.AccountDto;
import com.yunusemrenalbant.banking.entity.Account;

public class AccountMapper {

    public static Account mapToAccount(AccountDto accountDto) {
    Account account = new Account(
            accountDto.id(),
            accountDto.holderName(),
            accountDto.balance());
        return account;
    }

    public static AccountDto mapToAccountDto(Account account) {
        AccountDto accountDto = new AccountDto(
            account.getId(),
            account.getHolderName(),
            account.getBalance()
        );

        return accountDto;
    }
}
