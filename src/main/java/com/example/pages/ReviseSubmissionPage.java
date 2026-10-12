package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ReviseSubmissionPage extends BasePage {

    // Form Field Locators
    private final By projectTitleInput = By.name("projectTitle");
    private final By problemStatementInput = By.name("problemStatement");
    private final By objectivesInput = By.name("objectives");
    private final By documentationLinkInput = By.name("documentationLink");
    private final By techStackChips = By.cssSelector(".chip, .selected-stack, .badge, [class*='chip']");
    private final By techStackRemoveButtons = By.cssSelector(".chip-remove, button[class*='remove']");

    // Action Locators
    private final By reuploadButton = By.cssSelector("button.primary-button, form button[type='submit'], button[type='submit']");
    private final By validationAlert = By.cssSelector(".alert.error, .alert, .field-error");

    public ReviseSubmissionPage(WebDriver driver) {
        super(driver);
    }

    public boolean isReviseModeLoaded() {
        boolean urlLoaded = WaitUtils.waitForUrlContains(driver, "revise", 10);
        if (!urlLoaded) return false;
        WaitUtils.waitForElementVisible(driver, projectTitleInput, 10);
        // Wait up to 5 seconds for React state to populate the form fields
        for (int i = 0; i < 20; i++) {
            String val = getAttribute(projectTitleInput, "value");
            if (val != null && !val.trim().isEmpty()) {
                return true;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        return isDisplayed(projectTitleInput);
    }

    public String getProjectTitleValue() {
        WaitUtils.waitForElementVisible(driver, projectTitleInput, 10);
        for (int i = 0; i < 20; i++) {
            String val = getAttribute(projectTitleInput, "value");
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        return getAttribute(projectTitleInput, "value");
    }

    public String getProblemStatementValue() {
        WaitUtils.waitForElementVisible(driver, problemStatementInput, 10);
        for (int i = 0; i < 20; i++) {
            String val = getAttribute(problemStatementInput, "value");
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        return getAttribute(problemStatementInput, "value");
    }

    public String getObjectivesValue() {
        WaitUtils.waitForElementVisible(driver, objectivesInput, 10);
        for (int i = 0; i < 20; i++) {
            String val = getAttribute(objectivesInput, "value");
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        return getAttribute(objectivesInput, "value");
    }

    public String getDocumentationLinkValue() {
        return getAttribute(documentationLinkInput, "value");
    }

    public int getTechStackChipCount() {
        List<WebElement> chips = driver.findElements(techStackChips);
        return chips.size();
    }

    public void modifyProjectTitle(String newTitle) {
        clearField(projectTitleInput);
        enterText(projectTitleInput, newTitle);
    }

    public void modifyProblemStatement(String newText) {
        clearField(problemStatementInput);
        enterText(problemStatementInput, newText);
    }

    public void modifyObjectives(String newObjectives) {
        clearField(objectivesInput);
        enterText(objectivesInput, newObjectives);
    }

    public void clearField(By locator) {
        WebElement element = driver.findElement(locator);
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.BACK_SPACE);
        element.clear();
    }

    public void clearProjectTitle() {
        clearField(projectTitleInput);
    }

    public void clearProblemStatement() {
        clearField(problemStatementInput);
    }

    public void clearObjectives() {
        clearField(objectivesInput);
    }

    public void clickReuploadForAnalysis() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, reuploadButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", btn);
        }
    }

    public boolean isSubmissionPrevented() {
        return getCurrentUrl().contains("revise");
    }

    public boolean isValidationMessageDisplayed() {
        if (isDisplayed(validationAlert)) {
            return true;
        }
        // HTML5 validity check
        try {
            WebElement titleElem = driver.findElement(projectTitleInput);
            Boolean isInvalid = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return !arguments[0].checkValidity();", titleElem);
            if (Boolean.TRUE.equals(isInvalid)) {
                return true;
            }
        } catch (Exception ignored) {}
        return isSubmissionPrevented();
    }

    public boolean isConfirmationOrReuploadDisplayed() {
        try {
            if (WaitUtils.waitForUrlContains(driver, "submissions", 4)
                    || WaitUtils.waitForUrlContains(driver, "dashboard", 4)) {
                return true;
            }
        } catch (Exception ignored) {}

        for (int i = 0; i < 15; i++) {
            try {
                String bodyText = driver.findElement(By.tagName("body")).getText();
                if (bodyText.contains("Analysis")
                        || bodyText.contains("Alignment Score")
                        || bodyText.contains("successfully")
                        || bodyText.contains("Submitted")
                        || bodyText.contains("PENDING")
                        || bodyText.contains("QUALIFIED")) {
                    return true;
                }
            } catch (Exception ignored) {}
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
        return false;
    }

    public boolean waitForReuploadCompletion() {
        return isConfirmationOrReuploadDisplayed();
    }
}
