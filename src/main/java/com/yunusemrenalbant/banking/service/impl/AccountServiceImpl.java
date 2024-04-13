package com.yunusemrenalbant.banking.service.impl;

import com.yunusemrenalbant.banking.dto.AccountDto;
import com.yunusemrenalbant.banking.dto.TransactionDto;
import com.yunusemrenalbant.banking.dto.TransferFundDto;
import com.yunusemrenalbant.banking.entity.Account;
import com.yunusemrenalbant.banking.entity.Transaction;
import com.yunusemrenalbant.banking.exception.AccountException;
import com.yunusemrenalbant.banking.mapper.AccountMapper;
import com.yunusemrenalbant.banking.repository.AccountRepository;
import com.yunusemrenalbant.banking.repository.TransactionRepository;
import com.yunusemrenalbant.banking.service.AccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";

    public AccountServiceImpl(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

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

        Transaction transaction = new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

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

      if (fromAccount.getBalance() < transferFundDto.amount()) {
          throw new RuntimeException("Insufficient Amount");
      }

      fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());
      toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

      accountRepository.save(fromAccount);
      accountRepository.save(toAccount);

      Transaction transaction = new Transaction();
      transaction.setAccountId(transferFundDto.fromAccountId());
      transaction.setAmount(transferFundDto.amount());
      transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
      transaction.setTimestamp(LocalDateTime.now());

      transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId);

        return transactions.stream()
                .map(transaction -> convertEntityToDto(transaction))
                .collect(Collectors.toList());
    }

    private TransactionDto convertEntityToDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp()
        );
    }
}
