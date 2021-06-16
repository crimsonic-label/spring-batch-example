package atd.test.springbatchexample;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloWorldTasklet implements Tasklet {

    private static final String HELLO_WORLD_MESSAGE = "Hello, %s\n";

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        String name = (String) chunkContext.getStepContext()
                .getJobParameters()
                .get("name");

        ExecutionContext jobExecutionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        jobExecutionContext.put("user.name", name);

        System.out.printf(HELLO_WORLD_MESSAGE, name);

        return RepeatStatus.FINISHED;
    }
}
