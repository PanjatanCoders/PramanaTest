package com.razatech.tests;

import com.razatech.base.BaseTest;
import com.razatech.pages.TablePage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TablePageTest extends BaseTest {
    @BeforeMethod
    public void navigate() throws InterruptedException {
        driver.get("https://panjatan.netlify.app/table");
        Thread.sleep(2000);
    }

    @Test
    public void verifyPageTitle() {
        TablePage table = new TablePage(driver);
        Assert.assertTrue(table.getTitle().contains("Container Shipment Data"), "Page title mismatch!");
    }

    @Test
    public void verifyRecordsPerPage() {
        TablePage table = new TablePage(driver);
        table.selectRecordsPerPage("20");
        Assert.assertTrue(table.getVisibleRowCount() <= 20, "Records per page validation failed!");
    }

    @Test
    public void verifyStatusFilter() {
        TablePage table = new TablePage(driver);
        table.filterByStatus("Delivered");
        for (String status : table.getAllStatuses()) {
            Assert.assertEquals(status, "Delivered", "Status filter not working correctly!");
        }
    }

    @Test
    public void verifyDownloadCSV() throws InterruptedException {
        TablePage table = new TablePage(driver);
        table.clickDownloadCSV();
        Thread.sleep(3000);

        File downloadDir = new File(System.getProperty("user.home") + "/Downloads");
        File[] files = downloadDir.listFiles((dir, name) -> name.endsWith(".csv"));
        Assert.assertTrue(files != null && files.length > 0, "CSV file not downloaded!");
    }

    @Test
    public void verifySortingByContainerID() {
        TablePage table = new TablePage(driver);
        table.sortByContainerID();

        List<String> actual = table.getContainerIDs();
        List<String> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        Assert.assertEquals(actual, expected, "Sorting by Container ID failed!");
    }
}
