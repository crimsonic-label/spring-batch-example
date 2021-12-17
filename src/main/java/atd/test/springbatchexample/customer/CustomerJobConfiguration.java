package atd.test.springbatchexample.customer;

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
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
public class CustomerJobConfiguration {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}") ClassPathResource inputFile) {
        log.info(" creating flat file item reader for {}", inputFile);

        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(inputFile)
                .fixedLength()
                .columns(new Range[]{
                        new Range(1, 10),
                        new Range(11, 11),
                        new Range(12, 21),
                        new Range(22, 25),
                        new Range(26, 46),
                        new Range(47, 62),
                        new Range(63, 64),
                        new Range(65, 69)
                })
                .names("firstName",
                        "middleInitial",
                        "lastName",
                        "addressNumber",
                        "street",
                        "city",
                        "state",
                        "zipCode")
                .targetType(Customer.class)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Customer> customerItemWriter() {
        return (items) -> items.forEach(item -> log.info("read item: {}", item));
    }

    @Bean
    public Step importCustomersStep() {
        return stepBuilderFactory.get("importCustomer")
                // chunk size of 100 records
                .<Customer, Customer>chunk(100)
                .reader(customerItemReader(null))
                .writer(customerItemWriter())
                .build();
    }

    /**
     * job importing customers from flat file - fixed length columns
     *
     * @return the job
     */
    @Bean
    public Job customerJob() {
        return jobBuilderFactory.get("customerJob")
                .incrementer(new DailyJobTimestamper())
                .start(importCustomersStep())
                .build();
    }
}
