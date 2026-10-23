package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SubmissionsPage extends BasePage {

    // Locators
    private final By submissionsNavTab = By.xpath("//a[contains(@href,'submissions') or contains(.,'Submissions')]");
    private final By submissionsHeader = By.xpath("//h1[contains(text(),'Submissions')] | //h2[contains(text(),'Submissions')]");
    private final By submissionCards = By.cssSelector("article.submission-card");
    private final By cardTitle = By.tagName("h2");
    private final By cardStatus = By.cssSelector(".status");
    private final By cardScore = By.cssSelector(".score strong");
    private final By cardAnalysis = By.xpath(".//strong[contains(text(),'Analysis')]/following-sibling::p");
    private final By cardMatchedCriteria = By.xpath(".//strong[contains(text(),'Matched Criteria')]/following-sibling::p");
    private final By cardMissingCriteria = By.xpath(".//strong[contains(text(),'Missing Criteria')]/following-sibling::p");

    public SubmissionsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToSubmissions() {
        WebElement nav = WaitUtils.waitForElementVisible(driver, submissionsNavTab, 10);
        try {
            nav.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nav);
        }
        WaitUtils.waitForUrlContains(driver, "submissions", 10);
        waitForSubmissionsToLoad();
    }

    public void waitForSubmissionsToLoad() {
        try {
            // Wait until cards are populated or at least 2 seconds after page load
            for (int i = 0; i < 20; i++) {
                List<WebElement> cards = driver.findElements(submissionCards);
                if (!cards.isEmpty()) {
                    break;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException ignored) {}
    }

    public boolean isSubmissionsPageDisplayed() {
        return WaitUtils.waitForUrlContains(driver, "submissions", 10);
    }

    public List<WebElement> getAllSubmissionCards() {
        waitForSubmissionsToLoad();
        return driver.findElements(submissionCards);
    }

    public boolean isProjectPresentInList(String projectTitle) {
        waitForSubmissionsToLoad();
        List<WebElement> cards = driver.findElements(submissionCards);
        for (WebElement card : cards) {
            try {
                String title = card.findElement(cardTitle).getText().trim();
                if (title.toLowerCase().contains(projectTitle.toLowerCase())) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public WebElement getSubmissionCardByTitle(String projectTitle) {
        waitForSubmissionsToLoad();
        List<WebElement> cards = driver.findElements(submissionCards);
        for (WebElement card : cards) {
            try {
                String title = card.findElement(cardTitle).getText().trim();
                if (title.toLowerCase().contains(projectTitle.toLowerCase())) {
                    return card;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public WebElement findProjectEvaluatedBelowThreshold(int thresholdPercent) {
        waitForSubmissionsToLoad();
        List<WebElement> cards = driver.findElements(submissionCards);
        for (WebElement card : cards) {
            try {
                String scoreText = card.findElement(cardScore).getText().replace("%", "").trim();
                int score = Integer.parseInt(scoreText);
                if (score < thresholdPercent) {
                    return card;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public String getCardStatus(WebElement card) {
        try {
            return card.findElement(cardStatus).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardScore(WebElement card) {
        try {
            return card.findElement(cardScore).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardAnalysis(WebElement card) {
        try {
            return card.findElement(cardAnalysis).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardMatchedCriteria(WebElement card) {
        try {
            return card.findElement(cardMatchedCriteria).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getCardMissingCriteria(WebElement card) {
        try {
            return card.findElement(cardMissingCriteria).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private final By reviseButtonLocator = By.cssSelector("a.revise-button, button.revise-button, a[href*='revise']");

    public boolean hasReviseOption() {
        waitForSubmissionsToLoad();
        List<WebElement> buttons = driver.findElements(reviseButtonLocator);
        return !buttons.isEmpty();
    }

    public List<WebElement> getAllReviseButtons() {
        waitForSubmissionsToLoad();
        return driver.findElements(reviseButtonLocator);
    }

    public void clickFirstReviseButton() {
        waitForSubmissionsToLoad();
        WebElement btn = WaitUtils.waitForElementVisible(driver, reviseButtonLocator, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", btn);
        }
        WaitUtils.waitForUrlContains(driver, "revise", 10);
    }

    public String getFirstReviseHref() {
        waitForSubmissionsToLoad();
        List<WebElement> buttons = driver.findElements(reviseButtonLocator);
        if (!buttons.isEmpty()) {
            return buttons.get(0).getAttribute("href");
        }
        return "";
    }

    public boolean isReviseButtonPresentForProject(String projectTitle) {
        WebElement card = getSubmissionCardByTitle(projectTitle);
        if (card != null) {
            List<WebElement> revBtns = card.findElements(reviseButtonLocator);
            return !revBtns.isEmpty();
        }
        return false;
    }

    public void clickReviseForProject(String projectTitle) {
        WebElement card = getSubmissionCardByTitle(projectTitle);
        if (card != null) {
            WebElement btn = card.findElement(reviseButtonLocator);
            try {
                btn.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", btn);
            }
            WaitUtils.waitForUrlContains(driver, "revise", 10);
        }
    }

    public String getCardTextByTitle(String projectTitle) {
        WebElement card = getSubmissionCardByTitle(projectTitle);
        if (card != null) {
            return card.getText().trim();
        }
        return "";
    }
}
