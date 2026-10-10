package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class NotificationsPage extends BasePage {

    // Locators
    private final By notificationsNavTab = By.xpath("//a[contains(@href,'notifications') or contains(.,'Notifications')]");
    private final By notificationsHeader = By.xpath("//h1[contains(.,'Notifications')] | //h2[contains(.,'Notifications')]");
    private final By refreshButton = By.xpath("//button[contains(.,'Refresh')]");
    private final By notificationCards = By.cssSelector("article.card.notification, article.notification");
    private final By unreadNotificationCards = By.cssSelector("article.card.notification.unread, article.unread");
    private final By notificationMessages = By.cssSelector(".notification-message, .notification-content");
    private final By notificationDates = By.cssSelector(".notification-date, span[class*='date'], time");
    private final By markAsReadButton = By.xpath(".//button[contains(.,'Mark as read')] | .//span[contains(.,'Mark as read')]");
    private final By readStatusIndicator = By.xpath(".//*[contains(text(),'Read')]");

    public NotificationsPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToNotifications() {
        WebElement nav = WaitUtils.waitForElementVisible(driver, notificationsNavTab, 10);
        try {
            nav.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nav);
        }
        WaitUtils.waitForUrlContains(driver, "notifications", 10);
        waitForNotificationsToLoad();
    }

    public void waitForNotificationsToLoad() {
        try {
            for (int i = 0; i < 20; i++) {
                List<WebElement> cards = driver.findElements(notificationCards);
                if (!cards.isEmpty()) {
                    break;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException ignored) {}
    }

    public boolean isNotificationsPageDisplayed() {
        return WaitUtils.waitForUrlContains(driver, "notifications", 10)
            && (isDisplayed(notificationsHeader) || !getAllNotificationCards().isEmpty());
    }

    public List<WebElement> getAllNotificationCards() {
        waitForNotificationsToLoad();
        return driver.findElements(notificationCards);
    }

    public int getNotificationCount() {
        return getAllNotificationCards().size();
    }

    public boolean areTimestampsDisplayed() {
        List<WebElement> dates = driver.findElements(notificationDates);
        if (dates.isEmpty()) return false;
        for (WebElement d : dates) {
            if (!d.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<String> getAllNotificationMessages() {
        waitForNotificationsToLoad();
        List<String> messages = new ArrayList<>();
        List<WebElement> elements = driver.findElements(notificationMessages);
        for (WebElement el : elements) {
            String txt = el.getText().trim();
            if (!txt.isEmpty()) {
                messages.add(txt);
            }
        }
        return messages;
    }

    public boolean hasLowScoreAlert() {
        for (String msg : getAllNotificationMessages()) {
            if (msg.contains("needs improvement") && msg.contains("Alignment score:")) {
                return true;
            }
        }
        return false;
    }

    public String getLowScoreAlertMessage() {
        for (String msg : getAllNotificationMessages()) {
            if (msg.contains("needs improvement") && msg.contains("Alignment score:")) {
                return msg;
            }
        }
        return "";
    }

    public boolean hasHighScoreAlert() {
        for (String msg : getAllNotificationMessages()) {
            if (msg.contains("qualified for trainer review") && msg.contains("alignment score of")) {
                return true;
            }
        }
        return false;
    }

    public String getHighScoreAlertMessage() {
        for (String msg : getAllNotificationMessages()) {
            if (msg.contains("qualified for trainer review") && msg.contains("alignment score of")) {
                return msg;
            }
        }
        return "";
    }

    public boolean hasTrainerDecisionAlert() {
        for (String msg : getAllNotificationMessages()) {
            if (msg.toLowerCase().contains("trainer decision for")) {
                return true;
            }
        }
        return false;
    }

    public String getTrainerDecisionAlertMessage() {
        for (String msg : getAllNotificationMessages()) {
            if (msg.toLowerCase().contains("trainer decision for")) {
                return msg;
            }
        }
        return "";
    }

    public boolean markFirstUnreadNotificationAsRead() {
        List<WebElement> unreadCards = driver.findElements(unreadNotificationCards);
        if (unreadCards.isEmpty()) {
            // Check if there are cards with Mark as read button
            List<WebElement> cards = getAllNotificationCards();
            for (WebElement card : cards) {
                List<WebElement> btns = card.findElements(markAsReadButton);
                if (!btns.isEmpty()) {
                    WebElement btn = btns.get(0);
                    try {
                        btn.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                    }
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    return true;
                }
            }
            return false;
        }

        WebElement targetCard = unreadCards.get(0);
        List<WebElement> btns = targetCard.findElements(markAsReadButton);
        if (!btns.isEmpty()) {
            WebElement btn = btns.get(0);
            try {
                btn.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            return true;
        }
        return false;
    }

    public boolean isAnyNotificationMarkedRead() {
        List<WebElement> cards = getAllNotificationCards();
        for (WebElement card : cards) {
            String cardText = card.getText();
            if (cardText.contains("Read") && !cardText.contains("Mark as read")) {
                return true;
            }
        }
        return false;
    }

    public void clickRefresh() {
        WebElement btn = WaitUtils.waitForElementVisible(driver, refreshButton, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        waitForNotificationsToLoad();
    }

    public boolean verifyPodIsolation(String assignedPod) {
        List<WebElement> cards = getAllNotificationCards();
        if (cards.isEmpty()) {
            return true;
        }

        String[] otherPods = { "Pod Beta", "Pod Gamma", "Pod Delta", "Pod Theta", "Pod Omega" };
        for (WebElement card : cards) {
            String text = card.getText();
            for (String otherPod : otherPods) {
                if (text.contains(otherPod)) {
                    return false;
                }
            }
        }
        return true;
    }
}
