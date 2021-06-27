package atd.test.springbatchexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchExampleApplication {

    Logger logger = LoggerFactory.getLogger(SpringBatchExampleApplication.class);
    private Random random = new Random();

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Flow preProcessingFlow() {
        return new FlowBuilder<Flow>("preProcessingFlow")
                .start(loadFileStep())
                .next(loadCustomerStep())
                .next(updateStartStep())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("flowJob")
                .incrementer(new DailyJobTimestamper())
                .start(preProcessingFlow())
                .next(runBatch())
                 .end()
                .build();
    }

    @Bean
    public Step loadFileStep() {
        return stepBuilderFactory.get("loadFileStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info("The stock file has been loaded");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step loadCustomerStep() {
        return stepBuilderFactory.get("loadCustomerStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info("The customer file has been loaded");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step updateStartStep() {
        return stepBuilderFactory.get("updateStartStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.warn("The start has been updated");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step runBatch() {
        return stepBuilderFactory.get("runBatch")
                .tasklet((contribution, chunkContext) -> {
                    logger.warn("The batch has been run");
                    return RepeatStatus.FINISHED;
                })
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
