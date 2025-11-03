package com.razatech.listeners;

import com.razatech.reporting.PramanaReporter;
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
    public void onTestSuccess(ITestResult result) {
        logTest(result, "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logTest(result, "failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logTest(result, "skipped");
    }

    @Override
    public void onFinish(ISuite suite) {
        PramanaReporter.completeSuite();
    }

    private void logTest(ITestResult result, String status) {
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

        PramanaReporter.logTestResult(
            testCaseId, testName, status, duration, errorMessage, stackTrace
        );
    }
}