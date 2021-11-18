package atd.test.springbatchexample.exploring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExplorerJobScheduler {

    @Autowired
    private Job explorerJob;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobLauncher jobLauncher;

    @Scheduled(cron = "0 0 * * * *")
    public void runScheduledJob() {
        log.info("Job scheduled...");
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(explorerJob)
                .toJobParameters();

        try {
            jobLauncher.run(explorerJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job execution already running", e);
        } catch (JobRestartException e) {
            log.error("Cannot restart job", e);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job instance already completed", e);
        } catch (JobParametersInvalidException e) {
            log.error("Job parameters invalid", e);
        }
    }
}
