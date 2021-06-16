package atd.test.springbatchexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Callable;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchExampleApplication {

    Logger logger = LoggerFactory.getLogger(SpringBatchExampleApplication.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job collableJob() {
        return jobBuilderFactory.get("callableStep")
                .incrementer(new DailyJobTimestamper())
                .start(callableStep())
                .build();
    }

    @Bean
    public Step callableStep() {
        return stepBuilderFactory.get("callableStep")
                .tasklet(callableTasklet())
                .build();
    }

    @Bean
    public CallableTaskletAdapter callableTasklet() {
        CallableTaskletAdapter callableTaskletAdapter = new CallableTaskletAdapter();
        callableTaskletAdapter.setCallable(callableObject());
        return callableTaskletAdapter;
    }

    private Callable<RepeatStatus> callableObject() {
        return () -> {
            logger.info("This is run in another thread: {}", Thread.currentThread().getName());
            return RepeatStatus.FINISHED;
        };
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
