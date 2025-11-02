package com.razatech.pages;

import org.openqa.selenium.*;

public class HomePage {
    private WebDriver driver;

    private By navLinks = By.cssSelector("nav a");
    private By heroHeading = By.cssSelector(".hero h1, .hero-title, h1");
    private By heroButton = By.xpath("//a[.='Start Practicing']");
    private By featureItems = By.cssSelector("section");
    private By footer = By.tagName("footer");
    private By footerLinks = By.cssSelector("footer a");
    private By socialIcons = By.cssSelector("footer a[target='_blank']");
    private By hamburgerMenu = By.cssSelector(".menu-toggle, .hamburger");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public int getNavLinkCount() {
        return driver.findElements(navLinks).size();
    }

    public void clickNavLink(String linkText) {
        driver.findElement(By.linkText(linkText)).click();
    }

    public boolean isHeroVisible() {
        return driver.findElement(heroHeading).isDisplayed();
    }

    public void clickHeroButton() {
        driver.findElement(heroButton).click();
    }

    public int getFeatureCount() {
        return driver.findElements(featureItems).size();
    }

    public boolean isFooterVisible() {
        return driver.findElement(footer).isDisplayed();
    }

    public int getFooterLinkCount() {
        return driver.findElements(footerLinks).size();
    }

    public int getSocialIconCount() {
        return driver.findElements(socialIcons).size();
    }

    public boolean isHamburgerMenuVisible() {
        try {
            return driver.findElement(hamburgerMenu).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void clickHamburgerMenu() {
        driver.findElement(hamburgerMenu).click();
    }
}
