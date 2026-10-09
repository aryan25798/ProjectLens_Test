package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DashboardPage extends BasePage {

    // Locators
    private final By signOutButton = By.xpath("//button[contains(.,'Sign out') or contains(text(),'Sign out')]");
    private final By sidebarWorkspace = By.cssSelector("aside, nav");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardLoaded() {
        return WaitUtils.waitForUrlContains(driver, "dashboard", 10) 
            || WaitUtils.waitForUrlContains(driver, "trainer", 10)
            || WaitUtils.waitForUrlContains(driver, "pod", 10)
            || WaitUtils.waitForUrlContains(driver, "reviews", 10);
    }

    public void logout() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, signOutButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            // Scroll into view & click via JavaScript if intercepted by responsive layout
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", btn);
        }
    }
}
