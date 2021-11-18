package atd.test.springbatchexample.transactions;

import atd.test.springbatchexample.transactions.db.AccountSummary;
import atd.test.springbatchexample.transactions.db.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

@AllArgsConstructor
public class TransactionApplierProcessor implements ItemProcessor<AccountSummary, AccountSummary> {

    private final TransactionRepository transactionRepository;

    /**
     * for every account summary passed to processor current balance will be incremented according to transactions
     *
     * @param accountSummary account cummary to be updated with transactions
     * @return the same account summary
     */
    @Override
    public AccountSummary process(AccountSummary accountSummary) {
        // add all transactions for every account summary
        transactionRepository.findTransactionByAccountNumber(accountSummary.getNumber())
                .forEach(transaction -> accountSummary.setCurrentBalance(
                        accountSummary.getCurrentBalance().add(transaction.getAmount())));

        return accountSummary;
    }
}
