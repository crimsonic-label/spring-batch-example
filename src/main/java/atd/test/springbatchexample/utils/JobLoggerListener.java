package atd.test.springbatchexample.utils;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobLoggerListener implements JobExecutionListener {

    private static final String START_MESSAGE = "%s is begining execution...\n";
    private static final String END_MESSAGE = "%s has completed with the status %s\n";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.printf(START_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.printf(END_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
    }
}
