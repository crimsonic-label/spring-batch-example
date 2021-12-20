package atd.test.springbatchexample.customer.with.transactions;

import lombok.*;

import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@XmlType( name = "transaction")
public class Transaction {
    private String accountNumber;
    private Date transactionDate;
    private BigDecimal amount;
}
