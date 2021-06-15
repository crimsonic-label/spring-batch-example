package atd.test.springbatchexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
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
    public Step step() {
        return stepBuilderFactory.get("step1")
                .tasklet(helloWorldTasklet(null)).build();
    }

    /**
     * step scope enables late binding of job parameters
     * bean is created until are in scope of an execution
     *
     * @param name job parameter taken from command line argument (or other sources)
     * @return tasklet
     */
    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) -> {
            System.out.printf("Hello %s!%n", name);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("basicJob")
                .start(step())
                .validator(compositeValidator())
                .build();
    }

    @Bean
    public JobParametersValidator defaultValidator() {
        // only parameter existence is to be checked with DefaultJobParametersValidator
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        // no other parameters are allowed, only fileName and name
        validator.setRequiredKeys(new String[] {"fileName"});
        validator.setOptionalKeys(new String[] {"name"});
        validator.afterPropertiesSet();
        return validator;
    }

    @Bean
    public JobParametersValidator compositeValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new ParametersValidator(), defaultValidator()));
        return validator;
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
