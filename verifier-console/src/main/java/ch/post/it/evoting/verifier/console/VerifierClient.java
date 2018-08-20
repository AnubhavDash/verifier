package ch.post.it.evoting.verifier.console;

import ch.post.it.evoting.verifier.console.dto.Status;
import ch.post.it.evoting.verifier.console.dto.Test;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class VerifierClient {

    private static Logger log = Logger.getLogger(VerifierClient.class);

    private static final String SERVER_PATH = "https://localhost:8443/api/";

    private RestTemplate restTemplate;

    public VerifierClient() {
        try {
            restTemplate = RestClientHelper.getRestClientSSL(null, null, null, null, null, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Status getCurrentStatus() {
        return restTemplate.getForObject(SERVER_PATH + "status", Status.class);
    }

    public List<Test> getTests() {
        return restTemplate.exchange(SERVER_PATH + "tests",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Test>>() {
                }).getBody();
    }

    public void process() {
        restTemplate.postForObject(SERVER_PATH + "tests", null, String.class);
    }

    public void reset() {
        restTemplate.postForObject(SERVER_PATH + "reset", null, String.class);
    }

    public boolean ping() {
        try {
            return restTemplate.getForObject(SERVER_PATH + "ping", Boolean.class);
        } catch (ResourceAccessException e) {
            return false;
        }
    }

    public void shutdown() {
        restTemplate.getForObject(SERVER_PATH + "shutdown", String.class);
    }
}
