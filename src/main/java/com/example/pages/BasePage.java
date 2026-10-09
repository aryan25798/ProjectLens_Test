package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasePage {
    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected void click(By locator) {
        WaitUtils.waitForElementClickable(driver, locator, 10).click();
    }

    protected void enterText(By locator, String text) {
        WebElement element = WaitUtils.waitForElementVisible(driver, locator, 10);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return WaitUtils.waitForElementVisible(driver, locator, 10).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected String getAttribute(By locator, String attributeName) {
        return WaitUtils.waitForElementVisible(driver, locator, 10).getAttribute(attributeName);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
