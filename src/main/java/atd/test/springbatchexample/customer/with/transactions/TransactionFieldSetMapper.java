package atd.test.springbatchexample.customer.with.transactions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

@Slf4j
public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {
    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) {
        log.debug("mapping for transaction: {}", fieldSet);
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(fieldSet.readString("accountNumber"));
        transaction.setAmount(fieldSet.readBigDecimal("amount"));
        transaction.setTransactionDate(fieldSet.readDate("transactionDate", "yyyy-MM-dd HH:mm:ss"));
        return transaction;
    }
}
