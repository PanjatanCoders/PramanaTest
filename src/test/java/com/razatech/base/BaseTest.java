package com.razatech.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    private ThreadLocal<Long> testStartTime = new ThreadLocal<>();

    @BeforeMethod
    public void setup() {
        // Capture test start time
        testStartTime.set(System.currentTimeMillis());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Capture test end time
        long endTime = System.currentTimeMillis();
        long startTime = testStartTime.get();

        // Send test result to API
        ReportUtils.sendTestResult(result, startTime, endTime);

        // Cleanup
        testStartTime.remove();

        if (driver != null) {
            driver.quit();
        }
    }
}
