package atd.test.springbatchexample.transactions.db;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@AllArgsConstructor
public class TransactionService {
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;

    public void createData() {
        AccountSummary accountSummary = AccountSummary.builder()
                .number("12345678")
                .currentBalance(new BigDecimal(12.59))
                .build();
        accountRepository.save(accountSummary);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal(9.99))
                .timestamp(new Date())
                .account(accountSummary)
                .build();
        transactionRepository.save(transaction);
    }
}
