package com.razatech.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import java.util.stream.Collectors;

public class TablePage {
    private WebDriver driver;

    private By recordsDropdown = By.id("rows-per-page");
    private By statusDropdown = By.id("status-filter");
    private By tableRows = By.xpath("//table/tbody/tr");
    private By statusCells = By.xpath("//table/tbody/tr/td[4]");
    private By downloadCSVBtn = By.id("download-csv");
    private By containerHeader = By.xpath("//th[contains(text(),'Container ID')]");
    private By containerIDs = By.xpath("//tbody/tr/td[1]");

    public TablePage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void selectRecordsPerPage(String value) {
        new Select(driver.findElement(recordsDropdown)).selectByVisibleText(value);
    }

    public int getVisibleRowCount() {
        return driver.findElements(tableRows).size();
    }

    public void filterByStatus(String status) {
        new Select(driver.findElement(statusDropdown)).selectByVisibleText(status);
    }

    public List<String> getAllStatuses() {
        return driver.findElements(statusCells)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public void clickDownloadCSV() {
        driver.findElement(downloadCSVBtn).click();
    }

    public void sortByContainerID() {
        driver.findElement(containerHeader).click();
    }

    public List<String> getContainerIDs() {
        return driver.findElements(containerIDs)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
