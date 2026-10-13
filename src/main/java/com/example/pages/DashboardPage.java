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
        for (int i = 0; i < 20; i++) {
            String url = driver.getCurrentUrl().toLowerCase();
            if (url.contains("dashboard") || url.contains("trainer") || url.contains("pod") || url.contains("reviews") || url.contains("submissions") || url.contains("notifications")) {
                return true;
            }
            if (isDisplayed(signOutButton) || isDisplayed(sidebarWorkspace)) {
                return true;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
        return false;
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
