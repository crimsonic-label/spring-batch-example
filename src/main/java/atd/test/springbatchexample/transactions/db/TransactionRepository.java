package atd.test.springbatchexample.transactions.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedQuery;
import java.util.List;

@Repository
@NamedQuery(name = "getTransactionByAccountNumber", query = "select Transaction as t where t.account.number = :accountNumber")
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTransactionByAccountNumber(String accountNumber);
}
