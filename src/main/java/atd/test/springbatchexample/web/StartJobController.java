package atd.test.springbatchexample.web;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping
    public ExitStatus runJob(@RequestBody JobLaunchRequest request)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
            JobParametersInvalidException, JobRestartException {
        // get job by bean name
        Job job = applicationContext.getBean(request.getName(), Job.class);

        // it is caller responsibility to prepare job parameters
        // use run incrementer to update run.id parameter (parameter is added if incrementer defined in the job)
        // run.id value is based on the last execution parameters
        // parameters applied to job are immutable
        JobParameters jobParameters = new JobParametersBuilder(request.getJobParameters(), jobExplorer)
                .getNextJobParameters(job)
                .toJobParameters();

        return jobLauncher.run(job, jobParameters).getExitStatus();
    }
}
