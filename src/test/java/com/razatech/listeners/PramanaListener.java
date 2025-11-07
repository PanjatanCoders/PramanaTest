package com.razatech.listeners;

import com.razatech.reporting.PramanaReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import java.util.Arrays;

public class PramanaListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        String suiteName = suite.getName();
        String environment = System.getProperty("env", "staging");

        PramanaReporter.createSuite(
            suiteName,
            environment,
            Arrays.asList("automated", "testng")
        );
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Log test at start to get testId for step logging
        String testCaseId = result.getMethod().getMethodName();
        String testName = result.getMethod().getDescription() != null
            ? result.getMethod().getDescription()
            : result.getMethod().getMethodName();

        String testId = PramanaReporter.logTestResult(
            testCaseId, testName, "running", 0, null, null
        );
        PramanaReporter.setCurrentTestId(testId);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logTest(result, "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testId = logTest(result, "failed");

        // Capture and attach screenshot on failure
        if (testId != null) {
            captureAndAttachScreenshot(result, testId);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logTest(result, "skipped");
    }

    @Override
    public void onFinish(ISuite suite) {
        PramanaReporter.completeSuite();
    }

    private String logTest(ITestResult result, String status) {
        String testCaseId = result.getMethod().getMethodName();
        String testName = result.getMethod().getDescription() != null
            ? result.getMethod().getDescription()
            : result.getMethod().getMethodName();

        long duration = result.getEndMillis() - result.getStartMillis();

        String errorMessage = null;
        String stackTrace = null;

        if (result.getThrowable() != null) {
            errorMessage = result.getThrowable().getMessage();
            stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
        }

        return PramanaReporter.logTestResult(
            testCaseId, testName, status, duration, errorMessage, stackTrace
        );
    }

    private void captureAndAttachScreenshot(ITestResult result, String testId) {
        try {
            Object testInstance = result.getInstance();
            WebDriver driver = (WebDriver) testInstance.getClass()
                .getDeclaredField("driver")
                .get(testInstance);

            if (driver != null) {
                String base64Screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BASE64);

                String screenshotName = result.getMethod().getMethodName() + "-failure.png";
                String description = "Screenshot captured on test failure";

                PramanaReporter.attachScreenshot(testId, null, screenshotName,
                    base64Screenshot, description);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to capture screenshot: " + e.getMessage());
        }
    }
}