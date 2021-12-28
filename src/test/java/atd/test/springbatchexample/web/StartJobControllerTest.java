package atd.test.springbatchexample.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void shouldRunImportCustomerWithTransactionsJob() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                    "name": "customerWithTransactionsJob",
                    "properties": {
                        "customerFile": "customer/customersWithTransactions.txt"
                    }
                }""";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/runJob", new HttpEntity<>(payload, headers), String.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void shouldRunImportCustomerXmlJob() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                    "name": "xmlCustomerJob",
                    "properties": {
                        "customerFile": "customer/customers.xml"
                    }
                }""";

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/runJob", new HttpEntity<>(payload, headers), String.class);
        assertNotNull(responseEntity.getBody());
    }

    @Test
    public void shouldRunDbCustomerJob() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                    "name": "customerDbJob",
                    "properties": {
                    }
                }""";

        ResponseEntity<ExitStatus> responseEntity = restTemplate.postForEntity("http://localhost:8080/runJob", new HttpEntity<>(payload, headers), ExitStatus.class);
        ExitStatus exitStatus = responseEntity.getBody();
        assertNotNull(exitStatus);
        assertEquals(org.springframework.batch.core.ExitStatus.COMPLETED.getExitCode(), exitStatus.getExitCode());
    }

    @Test
    public void shouldRunServiceAdapterCustomerJob() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                    "name": "adapterCustomerJob",
                    "properties": {
                    }
                }""";

        ResponseEntity<ExitStatus> responseEntity = restTemplate.postForEntity("http://localhost:8080/runJob", new HttpEntity<>(payload, headers), ExitStatus.class);
        ExitStatus exitStatus = responseEntity.getBody();
        assertNotNull(exitStatus);
        assertEquals(org.springframework.batch.core.ExitStatus.COMPLETED.getExitCode(), exitStatus.getExitCode());
    }
}