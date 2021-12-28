package atd.test.springbatchexample.customer.db;

import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@Slf4j
public class DbCustomerJobConfiguration {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public JdbcCursorItemReader<CustomerEntity> customerJdbcItemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<CustomerEntity>()
                .name("customerItemReader")
                .dataSource(dataSource)
                .sql("select * from customer")
                .rowMapper(new CustomerRowMapper())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<CustomerEntity> jdbcPagingItemReader(DataSource dataSource) {
        return new JdbcPagingItemReaderBuilder<CustomerEntity>()
                .name("customerItemReader")
                .dataSource(dataSource)
                .queryProvider(pagingQueryProvider())
                .pageSize(10)
                .rowMapper(new CustomerRowMapper())
                .build();
    }

    @Bean
    public RepositoryItemReader<CustomerEntity> repositoryCustomerItemReader(CustomerRepository repository) {
        return new RepositoryItemReaderBuilder<CustomerEntity>()
                .name("customerItemReader")
                .repository(repository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .methodName("findAll")
                .build();
    }

    @Bean
    public PagingQueryProvider pagingQueryProvider() {
        H2PagingQueryProvider h2PagingQueryProvider = new H2PagingQueryProvider();
        h2PagingQueryProvider.setSelectClause("select *");
        h2PagingQueryProvider.setFromClause("from customer");
        h2PagingQueryProvider.setSortKeys(Map.of("id", Order.ASCENDING));
        return h2PagingQueryProvider;
    }

    /**
     * job importing customers from flat file - fixed length columns
     *
     * @return the job
     */
    @Bean
    public Job customerDbJob() {
        return jobBuilderFactory.get("customerDbJob")
                .incrementer(new DailyJobTimestamper())
                .start(importDbCustomerStep())
                .build();
    }

    @Bean
    public Step importDbCustomerStep() {
        return stepBuilderFactory.get("importDbCustomer")
                .chunk(100)
                .reader(repositoryCustomerItemReader(null))
                .writer(dbCustomerItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter dbCustomerItemWriter() {
        return (items) -> items.forEach(item -> log.info("read item: {}", item));
    }
}
