package atd.test.springbatchexample.customer.db;

import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
                .reader(customerJdbcItemReader(null))
                .writer(dbCustomerItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter dbCustomerItemWriter() {
        return (items) -> items.forEach(item -> log.info("read item: {}", item));
    }
}
