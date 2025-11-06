package com.razatech.tests;

import com.razatech.base.BaseTest;
import com.razatech.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {
    @BeforeMethod
    public void navigate() throws InterruptedException {
        driver.get("https://panjatan.netlify.app/");
        Thread.sleep(2000);
    }

    @Test(description = "Navigation Links Verification")
    public void verifyNavigationLinks() throws InterruptedException {
        HomePage home = new HomePage(driver);
        int count = home.getNavLinkCount();
        Assert.assertTrue(count >= 3, "Expected at least 3 navigation links but found: " + count);
    }

    @Test(description = "Hero Section Verification")
    public void verifyHeroSectionAndCTA() {
        HomePage home = new HomePage(driver);
        Assert.assertTrue(home.isHeroVisible(), "Hero heading not visible!");
        home.clickHeroButton();
        Assert.assertFalse(driver.getCurrentUrl().contains("learn") || driver.getCurrentUrl().contains("#"),
                "CTA button did not perform expected navigation.");
    }

    @Test(description = "Verify Features are available")
    public void verifyFeaturesSection() {
        HomePage home = new HomePage(driver);
        int featureCount = home.getFeatureCount();
        Assert.assertTrue(featureCount >= 3, "Expected at least 3 features but found: " + featureCount);
    }

    @Test(description = "Footer content links verification")
    public void verifyFooterContentAndLinks() {
        HomePage home = new HomePage(driver);
        Assert.assertTrue(home.isFooterVisible(), "Footer not visible!");
        Assert.assertTrue(home.getFooterLinkCount() > 0, "No footer links found!");
    }

}
