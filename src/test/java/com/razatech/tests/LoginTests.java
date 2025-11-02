package com.razatech.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class LoginTests {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

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

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}