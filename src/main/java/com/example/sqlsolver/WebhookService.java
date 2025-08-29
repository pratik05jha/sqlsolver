package com.example.sqlsolver;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void startProcess() {
        // Step 1: Call generateWebhook API
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Pratik Kumar Jha");
        requestBody.put("regNo", "22BCE7511");
        requestBody.put("email", "pratik.22bce7511@vitapstudent.ac.in");


        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");


            String finalQuery = "SELECT p.amount AS SALARY, " +
                    "CONCAT(e.first_name, ' ', e.last_name) AS NAME, " +
                    "TIMESTAMPDIFF(YEAR, e.dob, CURDATE()) AS AGE, " +
                    "d.department_name " +
                    "FROM payments p " +
                    "JOIN employee e ON p.emp_id = e.emp_id " +
                    "JOIN department d ON e.department = d.department_id " +
                    "WHERE DAY(p.payment_time) <> 1 " +
                    "ORDER BY p.amount DESC LIMIT 1;";


            sendFinalQuery(webhookUrl, accessToken, finalQuery);
        } else {
            System.out.println("Failed to generate webhook");
        }
    }

    private void sendFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // JWT auth

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println("Response from webhook: " + response.getBody());
    }
}
