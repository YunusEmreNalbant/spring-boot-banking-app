package com.yunusemrenalbant.banking.service.impl;

import com.yunusemrenalbant.banking.dto.AccountDto;
import com.yunusemrenalbant.banking.dto.TransferFundDto;
import com.yunusemrenalbant.banking.entity.Account;
import com.yunusemrenalbant.banking.exception.AccountException;
import com.yunusemrenalbant.banking.mapper.AccountMapper;
import com.yunusemrenalbant.banking.repository.AccountRepository;
import com.yunusemrenalbant.banking.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
       Account account = accountRepository
               .findById(id)
               .orElseThrow(() -> new AccountException("Account does not exists"));

       return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        double total = account.getBalance() + amount;

        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        if (account.getBalance() < amount) {
            throw new AccountException("Insufficient amount");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);
        Account savedAccount =  accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
       return accounts.stream().map((account -> AccountMapper.mapToAccountDto(account)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        accountRepository.deleteById(id);

    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {

      Account fromAccount = accountRepository
                .findById(transferFundDto.fromAccountId())
                .orElseThrow(() -> new AccountException("Account does not exists"));

      Account toAccount = accountRepository
              .findById(transferFundDto.toAccountId())
              .orElseThrow(() -> new AccountException("Account does not exists"));

      fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());
      toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

      accountRepository.save(fromAccount);
      accountRepository.save(toAccount);
    }
}
