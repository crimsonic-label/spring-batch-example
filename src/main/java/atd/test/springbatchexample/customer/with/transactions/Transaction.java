package atd.test.springbatchexample.customer.with.transactions;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Transaction {
    private String accountNumber;
    private Date transactionDate;
    private BigDecimal amount;
}
