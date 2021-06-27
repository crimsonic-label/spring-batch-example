package atd.test.springbatchexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
        return jobBuilderFactory.get("endStatusTestJob")
                .incrementer(new DailyJobTimestamper())
                // first step ended with failure
                //  job status ABANDONED, exit code FAILED, job status STOPPED, job exit code: STOPPED
                .start(firstStep())
                .on("FAILED").stopAndRestart(successStep())
                // running the second time job skips to success step (with error message that the fist step is not restartable)
                //  then job status COMPLETED, exit code COMPLETED, job status COMPLETED, job exit code: COMPLETED
                .from(firstStep()).on("*").to(successStep())
                .end()
                .build();
    }

    @Bean
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info("pass step!");
                    //return RepeatStatus.FINISHED;
                    throw new RuntimeException("Causing a failure");
                })
                .build();
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
