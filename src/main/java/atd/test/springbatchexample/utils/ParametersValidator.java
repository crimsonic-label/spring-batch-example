package atd.test.springbatchexample.utils;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

/**
 * parameter Validator for parameter requirements
 * to connect more than one validator, use CompositeJobParametersValidator
 */
public class ParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String fileName = jobParameters.getString("fileName");

        if(!StringUtils.hasText(fileName)) {
            throw new JobParametersInvalidException("fileName parameter is missing");
        } else if(!StringUtils.endsWithIgnoreCase(fileName, ".csv")) {
            throw new JobParametersInvalidException("file name parameter does not use the csv file extension");
        }
    }
}
