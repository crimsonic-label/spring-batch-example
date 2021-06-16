package atd.test.springbatchexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Random;

/**
 * set chunk size to random value between 0 and 1000
 */
public class RandomChunkSizePolicy implements CompletionPolicy {

    Logger logger = LoggerFactory.getLogger(RandomChunkSizePolicy.class);

    private int chunkSize;
    private int totalProcessed;
    private Random random = new Random();


    @Override
    public boolean isComplete(RepeatContext repeatContext, RepeatStatus repeatStatus) {
        return RepeatStatus.FINISHED == repeatStatus || isComplete(repeatContext);
    }

    @Override
    public boolean isComplete(RepeatContext repeatContext) {
        return totalProcessed >= chunkSize;
    }

    @Override
    public RepeatContext start(RepeatContext repeatContext) {
        chunkSize = random.nextInt(1000);
        totalProcessed = 0;
        logger.info("The chunk size has been set to {}", chunkSize);

        return repeatContext;
    }

    @Override
    public void update(RepeatContext repeatContext) {
        totalProcessed++;
    }
}
