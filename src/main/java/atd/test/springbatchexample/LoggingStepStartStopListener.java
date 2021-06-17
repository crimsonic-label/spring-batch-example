package atd.test.springbatchexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class LoggingStepStartStopListener {
    Logger logger = LoggerFactory.getLogger(LoggingStepStartStopListener.class);
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.info("{} has begun!", stepExecution.getStepName());
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("{} has ended!", stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }
}
