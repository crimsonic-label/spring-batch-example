package atd.test.springbatchexample.web;

import atd.test.springbatchexample.transactions.db.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TransactionsController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping(path = "/loadTransactions")
    public void loadTransactions() {
        transactionService.createData();
        log.info("Transactions loaded");
    }
}
