package com.razatech.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReportUtils {

    private static final String CONFIG_FILE = "config.properties";
    private static String API_BASE_URL;
    private static String SUITE_ID;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = ReportUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                API_BASE_URL = properties.getProperty("api.base.url", "http://localhost:9090");
                SUITE_ID = properties.getProperty("suite.id", "default-suite-id");
            } else {
                // Default values if config file not found
                API_BASE_URL = "http://localhost:9090";
                SUITE_ID = System.getProperty("suite.id", "default-suite-id");
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load config.properties. Using default values.");
            API_BASE_URL = "http://localhost:9090";
            SUITE_ID = System.getProperty("suite.id", "default-suite-id");
        }
    }

    public static void sendTestResult(ITestResult result, long startTimeMillis, long endTimeMillis) {
        try {
            String status = getTestStatus(result);
            long duration = endTimeMillis - startTimeMillis;

            String testCaseId = getTestCaseId(result);
            String testName = getTestName(result);
            String startTime = formatTimestamp(startTimeMillis);
            String endTime = formatTimestamp(endTimeMillis);

            Map<String, Object> testData = new HashMap<>();
            testData.put("testCaseId", testCaseId);
            testData.put("testName", testName);
            testData.put("status", status);
            testData.put("duration", duration);
            testData.put("startTime", startTime);
            testData.put("endTime", endTime);

            sendPostRequest(testData);

            System.out.println("Test result sent to API: " + testName + " - " + status);

        } catch (Exception e) {
            System.err.println("Failed to send test result to API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getTestStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "passed";
            case ITestResult.FAILURE:
                return "failed";
            case ITestResult.SKIP:
                return "skipped";
            default:
                return "unknown";
        }
    }

    private static String getTestCaseId(ITestResult result) {
        // Try to get test case ID from test method annotation or parameters
        // For now, generate from method name
        String className = result.getTestClass().getName();
        String methodName = result.getMethod().getMethodName();
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        return simpleClassName + "_" + methodName;
    }

    private static String getTestName(ITestResult result) {
        String description = result.getMethod().getDescription();
        String methodName = result.getMethod().getMethodName();

        if (description != null && !description.isEmpty()) {
            return description;
        }

        // Convert camelCase to readable format
        return methodName.replaceAll("([A-Z])", " $1")
                         .replaceAll("^test ", "")
                         .trim();
    }

    private static String formatTimestamp(long timestampMillis) {
        Instant instant = Instant.ofEpochMilli(timestampMillis);
        return DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneOffset.UTC)
                .format(instant);
    }

    private static void sendPostRequest(Map<String, Object> testData) throws IOException {
        String apiUrl = API_BASE_URL + "/api/v1/suites/" + SUITE_ID + "/tests";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            // Convert test data to JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonPayload = gson.toJson(testData);

            // Set request headers and body
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();

                if (statusCode >= 200 && statusCode < 300) {
                    System.out.println("API Response: " + statusCode + " - Test result submitted successfully");
                } else {
                    System.err.println("API Response: " + statusCode + " - Failed to submit test result");
                }
            }
        }
    }

    public static void setSuiteId(String suiteId) {
        SUITE_ID = suiteId;
    }

    public static void setApiBaseUrl(String baseUrl) {
        API_BASE_URL = baseUrl;
    }

    public static void completeSuite() {
        try {
            String apiUrl = API_BASE_URL + "/api/v1/suites/" + SUITE_ID + "/complete";

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPut httpPut = new HttpPut(apiUrl);
                httpPut.setHeader("Content-Type", "application/json");

                // Execute PUT request
                try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                    int statusCode = response.getCode();

                    if (statusCode >= 200 && statusCode < 300) {
                        System.out.println("API Response: " + statusCode + " - Suite marked as complete successfully");
                    } else {
                        System.err.println("API Response: " + statusCode + " - Failed to mark suite as complete");
                    }
                }
            }

            System.out.println("Suite completion sent to API for suite: " + SUITE_ID);

        } catch (Exception e) {
            System.err.println("Failed to send suite completion to API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
