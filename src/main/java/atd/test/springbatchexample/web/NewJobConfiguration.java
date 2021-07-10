package atd.test.springbatchexample.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NewJobConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private Step explorerStep;

    @Bean
    public Job examinerJob() {
        return jobBuilderFactory.get("examinerJob")
                // incrementer to update run.id parameter,
                //  parameters should be set before applying to a job
                .incrementer(new RunIdIncrementer())
                .start(explorerStep)
                .build();
    }
}
