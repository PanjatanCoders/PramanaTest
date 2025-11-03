package com.razatech.tests;

import com.razatech.base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTests extends BaseTest {

    @Test(priority = 1, description = "Test login with valid credentials")
    public void testLoginWithValidCredentials() {
        driver.get("https://panjatan.netlify.app/");
        System.out.println(driver.getTitle());
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.xpath("//button[.='Login']")).click();
    }

    @Test(priority = 2, description = "Test login with invalid credentials")
    public void testLoginWithInvalidCredentials() {
        driver.get("https://panjatan.netlify.app/");

        driver.findElement(By.id("username")).sendKeys("invalid");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.xpath("//button[.='Login']")).click();

        String errorMessage = driver.findElement(By.id("loginAlert")).getText();
        System.out.println(errorMessage);
        Assert.assertEquals(errorMessage, "Invalid username and password! Please check your credentials.",
                "Error message should be displayed");
    }

    @Test(priority = 3, description = "Test login with empty fields")
    public void testLoginWithEmptyFields() {
        driver.get("https://panjatan.netlify.app/");

        driver.findElement(By.xpath("//button[.='Login']")).click();

        boolean isErrorDisplayed = driver.findElement(By.id("loginAlert")).isDisplayed();
        Assert.assertTrue(isErrorDisplayed, "Invalid username and password! Please check your credentials.");
    }
}