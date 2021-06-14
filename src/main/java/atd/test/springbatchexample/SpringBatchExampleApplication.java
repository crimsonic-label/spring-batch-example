package atd.test.springbatchexample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
                .tasklet(helloWorldTasklet(null)).build();
    }

    /**
     * step scope enables late binding of job parameters
     * bean is created until are in scope of an execution
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
