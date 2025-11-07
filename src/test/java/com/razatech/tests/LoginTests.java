package com.razatech.tests;

import com.razatech.base.BaseTest;
import com.razatech.listeners.PramanaListener;
import com.razatech.reporting.PramanaReporter;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

@Listeners(PramanaListener.class)
public class LoginTests extends BaseTest {

    @Test(priority = 1, description = "Test login with valid credentials")
    public void testLoginWithValidCredentials() {
        long startTime = System.currentTimeMillis();
        driver.get("https://panjatan.netlify.app/");
        System.out.println(driver.getTitle());
        PramanaReporter.logStep("Navigate to login page", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.id("username")).sendKeys("admin");
        PramanaReporter.logStep("Enter username", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.id("password")).sendKeys("password");
        PramanaReporter.logStep("Enter password", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.xpath("//button[.='Login']")).click();
        PramanaReporter.logStep("Click login button", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(priority = 2, description = "Test login with invalid credentials")
    public void testLoginWithInvalidCredentials() {
        long startTime = System.currentTimeMillis();
        driver.get("https://panjatan.netlify.app/");
        PramanaReporter.logStep("Navigate to login page", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.id("username")).sendKeys("invalid");
        PramanaReporter.logStep("Enter invalid username", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        PramanaReporter.logStep("Enter invalid password", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.xpath("//button[.='Login']")).click();
        PramanaReporter.logStep("Click login button", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        String errorMessage = driver.findElement(By.id("loginAlert")).getText();
        System.out.println(errorMessage);
        Assert.assertEquals(errorMessage, "Invalid username and password! Please check your credentials.",
                "Error message should be displayed");
        PramanaReporter.logStep("Verify error message displayed", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(priority = 3, description = "Test login with empty fields")
    public void testLoginWithEmptyFields() {
        long startTime = System.currentTimeMillis();
        driver.get("https://panjatan.netlify.app/");
        PramanaReporter.logStep("Navigate to login page", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        driver.findElement(By.xpath("//button[.='Login']")).click();
        PramanaReporter.logStep("Click login button without entering credentials", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        boolean isErrorDisplayed = driver.findElement(By.id("loginAlert")).isDisplayed();
        Assert.assertTrue(isErrorDisplayed, "Invalid username and password! Please check your credentials.");
        PramanaReporter.logStep("Verify error message is displayed", "passed",
            System.currentTimeMillis() - startTime);
    }
}