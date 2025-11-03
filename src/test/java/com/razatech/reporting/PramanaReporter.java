package com.razatech.reporting;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PramanaReporter {

    private static final String CONFIG_FILE = "config.properties";
    private static String BASE_URL;
    private static String currentSuiteId = null;
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = PramanaReporter.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                BASE_URL = properties.getProperty("api.base.url", "http://localhost:9090");
                String configuredSuiteId = properties.getProperty("suite.id");

                // Allow runtime override via system property
                currentSuiteId = System.getProperty("suite.id", configuredSuiteId);

                System.out.println("Pramana Configuration Loaded:");
                System.out.println("  API Base URL: " + BASE_URL);
                if (currentSuiteId != null && !currentSuiteId.isEmpty()) {
                    System.out.println("  Using existing Suite ID: " + currentSuiteId);
                }
            } else {
                // Default values if config file not found
                BASE_URL = "http://localhost:9090";
                System.out.println("⚠️ config.properties not found. Using default BASE_URL: " + BASE_URL);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Could not load config.properties: " + e.getMessage());
            BASE_URL = "http://localhost:9090";
        }
    }

    public static String createSuite(String name, String environment, List<String> tags) {
        // If suite ID already configured, use it instead of creating a new one
        if (currentSuiteId != null && !currentSuiteId.isEmpty()) {
            System.out.println("ℹ️ Using pre-configured Suite ID from config.properties");
            return currentSuiteId;
        }

        // Otherwise, create a new suite
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + "/api/v1/suites");
            request.setHeader("Content-Type", "application/json");

            Map<String, Object> body = Map.of(
                "name", name,
                "environment", environment,
                "tags", tags
            );

            request.setEntity(new StringEntity(mapper.writeValueAsString(body)));

            String response = client.execute(request, r -> {
                return new String(r.getEntity().getContent().readAllBytes());
            });

            Map<String, Object> result = mapper.readValue(response, Map.class);
            currentSuiteId = (String) result.get("id");

            System.out.println("✅ Pramana Suite Created: " + currentSuiteId);
            return currentSuiteId;

        } catch (Exception e) {
            System.err.println("❌ Failed to create Pramana suite: " + e.getMessage());
            return null;
        }
    }

    public static String getCurrentSuiteId() {
        return currentSuiteId;
    }

    public static void logTestResult(String testCaseId, String testName,
                                      String status, long duration,
                                      String errorMessage, String stackTrace) {
        if (currentSuiteId == null) {
            System.err.println("⚠️ No active suite. Call createSuite() first.");
            return;
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(
                BASE_URL + "/api/v1/suites/" + currentSuiteId + "/tests"
            );
            request.setHeader("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("testCaseId", testCaseId);
            body.put("testName", testName);
            body.put("status", status);
            body.put("duration", duration);
            body.put("startTime", java.time.Instant.now().toString());

            if (errorMessage != null) {
                body.put("errorMessage", errorMessage);
            }
            if (stackTrace != null) {
                body.put("stackTrace", stackTrace);
            }

            request.setEntity(new StringEntity(mapper.writeValueAsString(body)));

            client.execute(request, r -> {
                System.out.println("✅ Test logged: " + testName + " [" + status + "]");
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to log test: " + e.getMessage());
        }
    }

    public static void completeSuite() {
        if (currentSuiteId == null) return;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(
                BASE_URL + "/api/v1/suites/" + currentSuiteId + "/complete"
            );

            client.execute(request, r -> {
                System.out.println("✅ Suite marked as complete");
                return null;
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to complete suite: " + e.getMessage());
        }
    }
}