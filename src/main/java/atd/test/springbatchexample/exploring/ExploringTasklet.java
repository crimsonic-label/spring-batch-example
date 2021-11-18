package atd.test.springbatchexample.exploring;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;


@Slf4j
public class ExploringTasklet implements Tasklet {
    private final JobExplorer jobExplorer;

    public ExploringTasklet(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        String jobName = chunkContext.getStepContext().getJobName();

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, Integer.MAX_VALUE);
        log.info("There are {} job instances for the job {}", jobInstances.size(), jobName);

        jobInstances.forEach(this::examineJobInstance);

        return RepeatStatus.FINISHED;
    }

    private void examineJobInstance(JobInstance jobInstance) {
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
        log.info("Instance {} had {} executions", jobInstance.getInstanceId(), jobExecutions.size());

        jobExecutions.forEach(jobExecution ->
                log.info("\tExecution {} resulted in exit status {}", jobExecution.getId(),
                        jobExecution.getExitStatus().getExitCode()));
    }
}
