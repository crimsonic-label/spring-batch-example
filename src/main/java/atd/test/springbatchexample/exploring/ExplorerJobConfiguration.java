package atd.test.springbatchexample.exploring;

import atd.test.springbatchexample.utils.DailyJobTimestamper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ExplorerJobConfiguration {
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
    @Bean(name = "explorerJob")
    public Job explorerJob() {
        return jobBuilderFactory.get("explorerJob")
                // incrementer to update run.id parameter,
                //  parameters should be set before applying to a job
                .incrementer(new DailyJobTimestamper())
                .start(explorerStep())
                .build();
    }

    /**
     * explorer step ro diplay jobs information
     *
     * @return the step
     */
    @Bean
    public Step explorerStep() {
        return stepBuilderFactory.get("exploringStep")
                .tasklet(new ExploringTasklet(this.jobExplorer))
                .build();
    }
}
