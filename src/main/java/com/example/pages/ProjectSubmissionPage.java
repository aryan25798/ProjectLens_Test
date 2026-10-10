package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class ProjectSubmissionPage extends BasePage {

    // Locators
    private final By submitNavTab = By.xpath("//a[contains(@href,'submit') or contains(.,'Submit idea')]");
    private final By formHeader = By.xpath("//h2[contains(.,'Submit a project idea') or contains(.,'Submit project idea')]");
    private final By activeCohortBadge = By.xpath("//*[contains(text(),'ACTIVE COHORT')]/following-sibling::* | //*[contains(text(),'AI-Assisted Engineering')]");
    private final By evaluationCriteriaSection = By.xpath("//*[contains(text(),'EVALUATION CRITERIA')]/.. | //*[contains(text(),'Java/Spring Boot, Angular')]");
    private final By minAlignmentRequirement = By.xpath("//*[contains(text(),'70%')]");

    // Form Field Locators
    private final By projectTitleInput = By.name("projectTitle");
    private final By problemStatementInput = By.name("problemStatement");
    private final By objectivesInput = By.name("objectives");
    private final By stackSelectDropdown = By.cssSelector(".stack-select");
    private final By stackOptions = By.cssSelector(".stack-option");
    private final By documentationLinkInput = By.name("documentationLink");
    private final By submitForAnalysisButton = By.cssSelector("button[type='submit']");

    // Validation Locators
    private final By validationAlert = By.cssSelector(".alert.error, .alert");
    private final By fieldErrors = By.cssSelector(".field-error");

    public ProjectSubmissionPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToSubmit() {
        WebElement nav = WaitUtils.waitForElementVisible(driver, submitNavTab, 10);
        try {
            nav.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nav);
        }
        WaitUtils.waitForUrlContains(driver, "submit", 10);
    }

    public boolean isSubmissionFormDisplayed() {
        return isDisplayed(formHeader) || isDisplayed(projectTitleInput);
    }

    public boolean isCohortCriteriaDisplayed() {
        return isDisplayed(activeCohortBadge) || isDisplayed(evaluationCriteriaSection);
    }

    public boolean isMinimumAlignmentRequirementDisplayed() {
        return isDisplayed(minAlignmentRequirement);
    }

    public String getCohortDetailsText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    public void enterProjectTitle(String title) {
        if (title != null && !title.isEmpty()) {
            enterText(projectTitleInput, title);
        }
    }

    public void enterProblemStatement(String problemStatement) {
        if (problemStatement != null && !problemStatement.isEmpty()) {
            enterText(problemStatementInput, problemStatement);
        }
    }

    public void enterObjectives(String objectives) {
        if (objectives != null && !objectives.isEmpty()) {
            enterText(objectivesInput, objectives);
        }
    }

    public void selectTechnologies(String commaSeparatedTechs) {
        if (commaSeparatedTechs == null || commaSeparatedTechs.trim().isEmpty()) {
            return;
        }

        // Open dropdown
        WebElement dropdown = WaitUtils.waitForElementVisible(driver, stackSelectDropdown, 10);
        try {
            dropdown.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}

        String[] techs = commaSeparatedTechs.split(",");
        List<WebElement> options = driver.findElements(stackOptions);

        for (String tech : techs) {
            String trimmedTech = tech.trim();
            for (WebElement opt : options) {
                if (opt.getText().trim().equalsIgnoreCase(trimmedTech)) {
                    try {
                        opt.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                    }
                    break;
                }
            }
        }

        // Close dropdown by clicking header or body
        try {
            driver.findElement(formHeader).click();
        } catch (Exception ignored) {}
    }

    public void enterDocumentationLink(String link) {
        if (link != null && !link.isEmpty()) {
            enterText(documentationLinkInput, link);
        }
    }

    public void clickSubmitForAnalysis() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, submitForAnalysisButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isValidationAlertDisplayed() {
        return isDisplayed(validationAlert);
    }

    public String getValidationAlertText() {
        try {
            return getText(validationAlert);
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getFieldErrors() {
        List<String> errors = new ArrayList<>();
        List<WebElement> elements = driver.findElements(fieldErrors);
        for (WebElement el : elements) {
            errors.add(el.getText().trim());
        }
        return errors;
    }

    public boolean isSubmissionBlocked() {
        return getCurrentUrl().contains("/submit");
    }
}
