package atd.test.springbatchexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
                .tasklet(helloWorldTasklet()).build();
    }

    @Bean
    public Tasklet helloWorldTasklet() {
        return (stepContribution, chunkContext) -> {
            String name = (String) chunkContext.getStepContext()
                    .getJobParameters()
                    .get("name");
            System.out.printf("Hello %s!%n", name);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("basicJob")
                .start(step())
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
