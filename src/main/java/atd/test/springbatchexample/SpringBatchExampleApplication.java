package atd.test.springbatchexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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
    public Job job() {
        return jobBuilderFactory.get("conditionalJob")
                .incrementer(new DailyJobTimestamper())
                .start(firstStep())
                .next(decider())
                .from(decider()).on(ExitStatus.FAILED.getExitCode()).to(failureStep())
                .from(firstStep()).on("*").to(successStep())
                .end()
                .build();
    }

    @Bean
    public Step firstStep() {
        return stepBuilderFactory.get("deciderStep")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return (jobExecution, stepExecution) ->
                random.nextBoolean() ? FlowExecutionStatus.COMPLETED : FlowExecutionStatus.FAILED;
    }


    @Bean
    public Step successStep() {
        return stepBuilderFactory.get("successStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info("Success!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step failureStep() {
        return stepBuilderFactory.get("failureStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.warn("Failure");
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
