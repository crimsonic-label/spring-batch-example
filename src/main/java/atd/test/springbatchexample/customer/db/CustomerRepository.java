package atd.test.springbatchexample.customer.db;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerRepository extends PagingAndSortingRepository<CustomerEntity, Long> {
}
