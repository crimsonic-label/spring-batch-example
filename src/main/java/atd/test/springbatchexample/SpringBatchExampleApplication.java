package atd.test.springbatchexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchExampleApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JobParametersValidator defaultValidator() {
        // only parameter existence is to be checked with DefaultJobParametersValidator
        // no other parameters are allowed, only fileName and name
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator(
                new String[]{"fileName"},
                // accept run.id parameter created by incrementer
                new String[]{"name", "currentDate"});
        validator.afterPropertiesSet();
        return validator;
    }

    @Bean
    public JobParametersValidator compositeValidator() throws Exception {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new ParametersValidator(), defaultValidator()));
        validator.afterPropertiesSet();
        return validator;
    }

    /**
     * promotion listener look for key "name" in step's execution context
     * when it is found after the step is successfully completed,
     * it will be copied into job execution context.
     * When not found, nothing happens
     *
     * @return the listener
     */
    @Bean
    public StepExecutionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"name"});
        return listener;
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1")
                .tasklet(new HelloWorldTasklet())
                .listener(promotionListener())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("basicJob")
                .start(step())
                .validator(compositeValidator())
                .incrementer(new DailyJobTimestamper())
                .listener(new JobLoggerListener())
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
