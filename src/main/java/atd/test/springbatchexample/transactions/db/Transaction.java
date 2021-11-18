package atd.test.springbatchexample.transactions.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "transactionIdGenerator")
    @SequenceGenerator(name = "transactionIdGenerator", sequenceName = "transaction_id_seq", allocationSize = 1)
    private Long id;
    private Date timestamp;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountSummary account;
}
