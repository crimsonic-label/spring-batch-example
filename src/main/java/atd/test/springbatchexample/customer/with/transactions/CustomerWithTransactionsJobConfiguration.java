package atd.test.springbatchexample.customer.with.transactions;

import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

@Configuration
@Slf4j
public class CustomerWithTransactionsJobConfiguration {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader customerWithTransactionsItemReader(@Value("#{jobParameters['customerFile']}") ClassPathResource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerWithTransactionsItemReader")
                .lineMapper(compositeLineTokenizer())
                .resource(inputFile)
                .build();
    }

    @Bean
    public PatternMatchingCompositeLineMapper compositeLineTokenizer() {
        Map<String, LineTokenizer> lineTokenizers = Map.of(
                "CUST*", customerDelimitedLineTokenizer(),
                "TRANS*", transactionLineTokenizer());

        BeanWrapperFieldSetMapper<Customer> customerFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        customerFieldSetMapper.setTargetType(Customer.class);
        customerFieldSetMapper.setDistanceLimit(1);

        Map<String, FieldSetMapper> fieldSetMappers = Map.of(
                "CUST*", customerFieldSetMapper,
                "TRANS*", new TransactionFieldSetMapper());

        PatternMatchingCompositeLineMapper lineMappers = new PatternMatchingCompositeLineMapper();
        lineMappers.setTokenizers(lineTokenizers);
        lineMappers.setFieldSetMappers(fieldSetMappers);
        return lineMappers;
    }

    @Bean
    public DelimitedLineTokenizer transactionLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("prefix", "accountNumber", "transactionDate", "amount");
        return lineTokenizer;
    }

    @Bean
    public DelimitedLineTokenizer customerDelimitedLineTokenizer() {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("firstName", "middleInitial", "lastName", "address", "city", "state", "zip");
        lineTokenizer.setIncludedFields(1, 2, 3, 4, 5, 6, 7);
        return lineTokenizer;
    }

    /**
     * job importing customers from flat file - fixed length columns
     *
     * @return the job
     */
    @Bean
    public Job customerWithTransactionsJob() {
        return jobBuilderFactory.get("customerWithTransactionsJob")
                .incrementer(new DailyJobTimestamper())
                .start(importCustomersWithTransactionsStep())
                .build();
    }

    @Bean
    public Step importCustomersWithTransactionsStep() {
        return stepBuilderFactory.get("importCustomerWithTransaction")
                .chunk(100)
                .reader(customerWithTransactionsItemReader(null))
                .writer(simpleCustomerItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter simpleCustomerItemWriter() {
        return (items) -> items.forEach(item -> log.info("read item: {}", item));
    }
}
