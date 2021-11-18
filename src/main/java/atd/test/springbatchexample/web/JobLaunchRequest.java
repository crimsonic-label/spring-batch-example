package atd.test.springbatchexample.web;

import lombok.Data;
import lombok.ToString;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Properties;

@Data
@ToString
public class JobLaunchRequest {
    private String name;
    private Properties properties;

    public JobParameters getJobParameters() {
        Properties newProperties = new Properties();
        newProperties.putAll(this.properties);
        return new JobParametersBuilder(newProperties).toJobParameters();
    }
}
