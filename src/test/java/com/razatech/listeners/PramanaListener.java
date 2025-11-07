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
        updateTest(result, "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        updateTest(result, "failed");

        // Capture and attach screenshot on failure
        String testId = PramanaReporter.getCurrentTestId();
        if (testId != null) {
            captureAndAttachScreenshot(result, testId);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        updateTest(result, "skipped");
    }

    @Override
    public void onFinish(ISuite suite) {
        PramanaReporter.completeSuite();
    }

    private void updateTest(ITestResult result, String status) {
        String testId = PramanaReporter.getCurrentTestId();
        if (testId == null) {
            System.err.println("⚠️ No active test to update");
            return;
        }

        long duration = result.getEndMillis() - result.getStartMillis();

        String errorMessage = null;
        String stackTrace = null;

        if (result.getThrowable() != null) {
            errorMessage = result.getThrowable().getMessage();
            stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
        }

        PramanaReporter.updateTestResult(testId, status, duration, errorMessage, stackTrace);
    }

    private void captureAndAttachScreenshot(ITestResult result, String testId) {
        try {
            Object testInstance = result.getInstance();

            // Try to get driver field from test class
            WebDriver driver = null;
            try {
                java.lang.reflect.Field driverField = testInstance.getClass().getSuperclass().getDeclaredField("driver");
                driverField.setAccessible(true);
                driver = (WebDriver) driverField.get(testInstance);
            } catch (NoSuchFieldException e) {
                // Try getting from the test class itself if not in superclass
                java.lang.reflect.Field driverField = testInstance.getClass().getDeclaredField("driver");
                driverField.setAccessible(true);
                driver = (WebDriver) driverField.get(testInstance);
            }

            if (driver != null) {
                String base64Screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BASE64);

                String screenshotName = result.getMethod().getMethodName() + "-failure.png";
                String description = "Screenshot captured on test failure";

                PramanaReporter.attachScreenshot(testId, null, screenshotName,
                    base64Screenshot, description);

                System.out.println("✅ Screenshot captured and attached: " + screenshotName);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to capture screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}