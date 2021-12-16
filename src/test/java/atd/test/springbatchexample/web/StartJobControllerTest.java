package atd.test.springbatchexample.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StartJobControllerTest {


    @Test
    public void shouldRunTransactionJob() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                    "name": "transactionJob",
                    "properties": {
                        "transactionFile": "transaction.csv",
                        "summaryFile": "account-summary.csv"
                    }
                }""";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/runJob", new HttpEntity<>(payload, headers), String.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void shouldRunCustomerJob() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                    "name": "customerJob",
                    "properties": {
                        "customerFile": "customer/customer.txt"
                    }
                }""";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/runJob", new HttpEntity<>(payload, headers), String.class);
        assertNotNull(responseEntity.getBody());
    }
}