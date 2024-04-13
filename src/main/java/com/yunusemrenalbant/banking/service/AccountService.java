package com.yunusemrenalbant.banking.service;

import com.yunusemrenalbant.banking.dto.AccountDto;
import com.yunusemrenalbant.banking.dto.TransactionDto;
import com.yunusemrenalbant.banking.dto.TransferFundDto;
import com.yunusemrenalbant.banking.entity.Transaction;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);

    AccountDto deposit(Long id, double amount);

    AccountDto withdraw(Long id, double amount);

    List<AccountDto> getAllAccounts();

    void deleteAccount(Long id);

    void transferFunds(TransferFundDto transferFundDto);

    List<TransactionDto> getAccountTransactions(Long accountId);
}
