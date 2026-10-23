package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ReviewsPage extends BasePage {

    // Locators
    private final By reviewsNavTab = By.xpath("//a[contains(@href,'reviews') or contains(.,'Reviews')]");
    private final By decisionQueueHeader = By.xpath("//h2[contains(text(),'Decision queue')]");
    private final By queueCountBadge = By.cssSelector(".queue-count, span[class*='queue']");
    private final By reviewItems = By.cssSelector("button.review-item");
    private final By detailPanel = By.cssSelector(".detail-panel, section[class*='detail']");
    private final By projectTitle = By.cssSelector(".detail-head h3, .detail-panel h3, h3");
    private final By bigScore = By.cssSelector(".big-score, span[class*='score']");
    private final By commentsTextarea = By.cssSelector("textarea[placeholder*='Add comments for the pod']");
    private final By approveButton = By.cssSelector("button.decision.approve, button[class*='approve']");
    private final By reviseButton = By.cssSelector("button.decision.revise, button[class*='revise']");
    private final By rejectButton = By.cssSelector("button.decision.reject, button[class*='reject']");
    private final By emptyQueueState = By.cssSelector(".empty, p:contains('All caught up')");

    public ReviewsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToReviews() {
        WebElement nav = WaitUtils.waitForElementVisible(driver, reviewsNavTab, 10);
        try {
            nav.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nav);
        }
        WaitUtils.waitForUrlContains(driver, "reviews", 10);
    }

    public boolean isDecisionQueueDisplayed() {
        return isDisplayed(decisionQueueHeader);
    }

    public String getQueueCountText() {
        try {
            return getText(queueCountBadge);
        } catch (Exception e) {
            return "";
        }
    }

    public int getQueueItemCount() {
        List<WebElement> items = driver.findElements(reviewItems);
        return items.size();
    }

    public void selectFirstSubmission() {
        List<WebElement> items = driver.findElements(reviewItems);
        if (!items.isEmpty()) {
            items.get(0).click();
        }
    }

    public boolean isSubmissionDetailsDisplayed() {
        return isDisplayed(detailPanel) || isDisplayed(projectTitle);
    }

    public String getProjectTitleText() {
        try {
            return getText(projectTitle);
        } catch (Exception e) {
            return "";
        }
    }

    public String getAlignmentScoreText() {
        try {
            return getText(bigScore);
        } catch (Exception e) {
            return "";
        }
    }

    public void enterComments(String comments) {
        enterText(commentsTextarea, comments);
    }

    public void clickApprove() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, approveButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void clickNeedsRevision() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, reviseButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void clickReject() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, rejectButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public boolean isQueueEmpty() {
        return isDisplayed(emptyQueueState) || getQueueItemCount() == 0;
    }

    public boolean isProjectInQueue(String projectTitleText) {
        List<WebElement> items = driver.findElements(reviewItems);
        for (WebElement item : items) {
            if (item.getText().toLowerCase().contains(projectTitleText.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void selectSubmissionByTitle(String projectTitleText) {
        List<WebElement> items = driver.findElements(reviewItems);
        for (WebElement item : items) {
            if (item.getText().toLowerCase().contains(projectTitleText.toLowerCase())) {
                item.click();
                return;
            }
        }
        if (!items.isEmpty()) {
            items.get(0).click();
        }
    }

    public String getDetailPanelContent() {
        try {
            return getText(detailPanel);
        } catch (Exception e) {
            return "";
        }
    }
}
