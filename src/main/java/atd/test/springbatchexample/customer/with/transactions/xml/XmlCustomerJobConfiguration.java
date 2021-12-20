package atd.test.springbatchexample.customer.with.transactions.xml;

import atd.test.springbatchexample.customer.with.transactions.Customer;
import atd.test.springbatchexample.customer.with.transactions.Transaction;
import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@Slf4j
public class XmlCustomerJobConfiguration {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    @StepScope
    public StaxEventItemReader<Customer> xmlCustomerItemReader(@Value("#{jobParameters['customerFile']}") ClassPathResource inputFile) {
        return new StaxEventItemReaderBuilder<Customer>()
                .name("xmlCustomerFileReader")
                .resource(inputFile)
                .addFragmentRootElements("customer")
                .unmarshaller(customerMarshaller())
                .build();
    }

    private Jaxb2Marshaller customerMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Customer.class, Transaction.class);
        return jaxb2Marshaller;
    }

    @Bean
    @StepScope
    public ItemWriter<Customer> xmlCustomerItemWriter() {
        return (items) -> items.forEach(item -> log.info("read item: {}", item));
    }

    @Bean
    public Step xmlImportCustomersStep() {
        return stepBuilderFactory.get("xmlImportCustomer")
                // chunk size of 100 records
                .<Customer, Customer>chunk(100)
                .reader(xmlCustomerItemReader(null))
                .writer(xmlCustomerItemWriter())
                .build();
    }

    /**
     * job importing customers from flat file - fixed length columns
     *
     * @return the job
     */
    @Bean
    public Job xmlCustomerJob() {
        return jobBuilderFactory.get("xmlCustomerJob")
                .incrementer(new DailyJobTimestamper())
                .start(xmlImportCustomersStep())
                .build();
    }
}
