package com.razatech.tests;

import com.razatech.base.BaseTest;
import com.razatech.listeners.PramanaListener;
import com.razatech.pages.TablePage;
import com.razatech.reporting.PramanaReporter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Listeners(PramanaListener.class)
public class TablePageTest extends BaseTest {
    @BeforeMethod
    public void navigate() throws InterruptedException {
        driver.get("https://panjatan.netlify.app/table");
        Thread.sleep(2000);
    }

    @Test(description = "Page title verification")
    public void verifyPageTitle() {
        long startTime = System.currentTimeMillis();
        TablePage table = new TablePage(driver);
        PramanaReporter.logStep("Initialize TablePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(table.getTitle().contains("Container Shipment Data"), "Page title mismatch!");
        PramanaReporter.logStep("Verify page title contains 'Container Shipment Data'", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Verify records per page")
    public void verifyRecordsPerPage() {
        long startTime = System.currentTimeMillis();
        TablePage table = new TablePage(driver);
        PramanaReporter.logStep("Initialize TablePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        table.selectRecordsPerPage("20");
        PramanaReporter.logStep("Select 20 records per page", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Assert.assertTrue(table.getVisibleRowCount() <= 20, "Records per page validation failed!");
        PramanaReporter.logStep("Verify visible row count is <= 20", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Verify Status Filter")
    public void verifyStatusFilter() {
        long startTime = System.currentTimeMillis();
        TablePage table = new TablePage(driver);
        PramanaReporter.logStep("Initialize TablePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        table.filterByStatus("Delivered");
        PramanaReporter.logStep("Apply status filter for 'Delivered'", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        for (String status : table.getAllStatuses()) {
            Assert.assertEquals(status, "Delivered", "Status filter not working correctly!");
        }
        PramanaReporter.logStep("Verify all visible rows have 'Delivered' status", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Verify download csv report")
    public void verifyDownloadCSV() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        TablePage table = new TablePage(driver);
        PramanaReporter.logStep("Initialize TablePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        table.clickDownloadCSV();
        PramanaReporter.logStep("Click download CSV button", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        Thread.sleep(3000);
        PramanaReporter.logStep("Wait for file download", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        File downloadDir = new File(System.getProperty("user.home") + "/Downloads");
        File[] files = downloadDir.listFiles((dir, name) -> name.endsWith(".csv"));
        Assert.assertTrue(files != null && files.length > 0, "CSV file not downloaded!");
        PramanaReporter.logStep("Verify CSV file is downloaded", "passed",
            System.currentTimeMillis() - startTime);
    }

    @Test(description = "Verify Sorting with container id")
    public void verifySortingByContainerID() {
        long startTime = System.currentTimeMillis();
        TablePage table = new TablePage(driver);
        PramanaReporter.logStep("Initialize TablePage object", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        table.sortByContainerID();
        PramanaReporter.logStep("Click to sort by Container ID", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        List<String> actual = table.getContainerIDs();
        PramanaReporter.logStep("Get container IDs from table", "passed",
            System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        List<String> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        Assert.assertEquals(actual, expected, "Sorting by Container ID failed!");
        PramanaReporter.logStep("Verify container IDs are sorted correctly", "passed",
            System.currentTimeMillis() - startTime);
    }
}
