package atd.test.springbatchexample.transactions;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class TransactionDto {
    private Long id;
    private Date timestamp;
    private BigDecimal amount;
    private String accountNumber;
}
