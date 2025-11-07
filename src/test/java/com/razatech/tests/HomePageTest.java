package com.razatech.tests;

import com.razatech.base.BaseTest;
import com.razatech.listeners.PramanaListener;
import com.razatech.pages.HomePage;
import com.razatech.reporting.PramanaReporter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(PramanaListener.class)
public class HomePageTest extends BaseTest {
    @BeforeMethod
    public void navigate() throws InterruptedException {
        driver.get("https://panjatan.netlify.app/");
        Thread.sleep(2000);
    }

    @Test(description = "Navigation Links Verification")
    public void verifyNavigationLinks() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        HomePage home = new HomePage(driver);
        PramanaReporter.logStep("Initialize HomePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        int count = home.getNavLinkCount();
        PramanaReporter.logStep("Get navigation link count", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(count >= 3, "Expected at least 3 navigation links but found: " + count);
        PramanaReporter.logStep("Verify at least 3 navigation links present", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Hero Section Verification")
    public void verifyHeroSectionAndCTA() {
        long startTime = System.currentTimeMillis();
        HomePage home = new HomePage(driver);
        PramanaReporter.logStep("Initialize HomePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(home.isHeroVisible(), "Hero heading not visible!");
        PramanaReporter.logStep("Verify hero section is visible", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        home.clickHeroButton();
        PramanaReporter.logStep("Click hero button (CTA)", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertFalse(driver.getCurrentUrl().contains("learn") || driver.getCurrentUrl().contains("#"),
                "CTA button did not perform expected navigation.");
        PramanaReporter.logStep("Verify navigation after CTA click", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Verify Features are available")
    public void verifyFeaturesSection() {
        long startTime = System.currentTimeMillis();
        HomePage home = new HomePage(driver);
        PramanaReporter.logStep("Initialize HomePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        int featureCount = home.getFeatureCount();
        PramanaReporter.logStep("Get features count", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(featureCount >= 3, "Expected at least 3 features but found: " + featureCount);
        PramanaReporter.logStep("Verify at least 3 features present", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Footer content links verification")
    public void verifyFooterContentAndLinks() {
        long startTime = System.currentTimeMillis();
        HomePage home = new HomePage(driver);
        PramanaReporter.logStep("Initialize HomePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(home.isFooterVisible(), "Footer not visible!");
        PramanaReporter.logStep("Verify footer is visible", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(home.getFooterLinkCount() > 0, "No footer links found!");
        PramanaReporter.logStep("Verify footer links are present", "passed",
            System.currentTimeMillis() - startTime);
    }

}
