package atd.test.springbatchexample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
@Slf4j
public class SpringBatchExampleApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobExplorer jobExplorer;

    /**
     * Job can be started with application when
     * spring.batch.job.enabled=true
     * and
     * spring.batch.job.names=job
     *
     * @return the job
     */
    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .incrementer(new DailyJobTimestamper())
                .start(explorerStep())
                .build();
    }

    @Bean
    public Step explorerStep() {
        return stepBuilderFactory.get("exploringStep")
                .tasklet(new ExploringTasklet(this.jobExplorer))
                .build();
    }

    /**
     * Running application with parameters pass the parameters into job instance
     *
     * @param args - args are passed to the job instance as parameters
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchExampleApplication.class, args);
    }
}
