package atd.test.springbatchexample.customer.adapter;

import atd.test.springbatchexample.customer.db.CustomerEntity;
import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AdapterCustomerJobConfiguration {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public ItemReaderAdapter<CustomerEntity> customerItemReaderAdapter(CustomerService customerService) {
        ItemReaderAdapter<CustomerEntity> adapter = new ItemReaderAdapter<>();
        adapter.setTargetObject(customerService);
        adapter.setTargetMethod("getCustomer");
        return adapter;
    }

    @Bean
    @StepScope
    public ItemWriter<CustomerEntity> adapterCustomerItemWriter() {
        return (items) -> items.forEach(item -> log.info("read item: {}", item));
    }

    @Bean
    public Step adapterCustomersStep() {
        return stepBuilderFactory.get("importCustomer")
                // chunk size of 10 records
                .<CustomerEntity, CustomerEntity>chunk(10)
                .reader(customerItemReaderAdapter(null))
                .writer(adapterCustomerItemWriter())
                .build();
    }

    /**
     * job importing customers from flat file - fixed length columns
     *
     * @return the job
     */
    @Bean
    public Job adapterCustomerJob() {
        return jobBuilderFactory.get("adapterCustomerJob")
                .incrementer(new DailyJobTimestamper())
                .start(adapterCustomersStep())
                .build();
    }
}
