package atd.test.springbatchexample.transactions.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accountIdGenerator" )
    @SequenceGenerator(name="accountIdGenerator", sequenceName="account_summary_id_seq", allocationSize = 1)
    private int id;
    private String number;
    private BigDecimal currentBalance;
}
