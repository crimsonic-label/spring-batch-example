package atd.test.springbatchexample.web;

import lombok.Data;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Properties;

@Data
public class JobLaunchRequest {
    private String name;
    private Properties properties;

    public JobParameters getJobParameters() {
        return new JobParametersBuilder(new Properties(properties)).toJobParameters();
    }
}
