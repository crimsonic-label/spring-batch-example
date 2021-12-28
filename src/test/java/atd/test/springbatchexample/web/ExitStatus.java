package atd.test.springbatchexample.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExitStatus {
    private String exitCode;
    private String exitDescription;

    @JsonCreator
    public ExitStatus(@JsonProperty("exitCode") String exitCode, @JsonProperty("exitDescription") String exitDescription) {
        this.exitCode = exitCode;
        this.exitDescription = exitDescription;
    }
}
