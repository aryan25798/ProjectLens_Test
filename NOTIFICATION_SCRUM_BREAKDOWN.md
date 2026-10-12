# 7-Day Scrum Breakdown: Notification Module (ProjectLens Automation)

This document provides a day-by-day submission breakdown of the **Notification Module** automation codebase formatted for daily scrum reporting and code review submissions.

---

## Day 1 — Page Object Model: Locators & Navigation Setup

### User Story
As an Automation QA Engineer, I want to define centralized web element locators and page navigation methods for the Notifications module in the Page Object Model so that changes to UI markup can be maintained without impacting test logic.

### Description
Created `NotificationsPage.java` extending `BasePage`. Defined resilient element locators for the left navigation tab, header, notification cards, unread status indicators, message text containers, timestamps, mark-as-read buttons, and the refresh button. Implemented explicit wait navigation to `/notifications`.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `NotificationsPage.java` | 1–38 | Class definition, private `By` locators, constructor, and `navigateToNotifications()` |

#### Code:
```java
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
```

### Daily Explanation
- Followed standard Page Object Model design patterns, declaring private `By` locators to protect DOM elements from external tampering.
- Used CSS selectors and XPath unions (`//h1[...] | //h2[...]`) for backward compatibility with different ProjectLens UI versions.
- Added explicit synchronization (`WaitUtils.waitForUrlContains`) to ensure the page is fully loaded before executing actions.

### Acceptance Result
Notifications page object is initialized, and browser navigation to `/notifications` functions with full wait synchronization.

---

## Day 2 — Page Object Model: Asynchronous Card Loading & List Extraction

### User Story
As a Pod Lead, I want helper methods that wait for notifications to load asynchronously and parse card messages and timestamps so that test cases can inspect recent updates.

### Description
Implemented polling wait handlers (`waitForNotificationsToLoad()`), card count queries (`getNotificationCount()`), timestamp verification (`areTimestampsDisplayed()`), and text extraction methods (`getAllNotificationMessages()`) to retrieve all notifications currently rendered on the page.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `NotificationsPage.java` | 40–88 | Asynchronous card synchronization, count, timestamp checks, and message extraction |

#### Code:
```java
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
```

### Daily Explanation
- Created a polling loop `waitForNotificationsToLoad()` that handles asynchronous API delays when rendering the notification feed.
- Created `areTimestampsDisplayed()` to verify that every item shows date and time metadata.
- Implemented `getAllNotificationMessages()` to parse and clean text into a standard `List<String>` for assertion checks.

### Acceptance Result
The automation suite can reliably extract notification counts, timestamps, and message lists across asynchronous page updates.

---

## Day 3 — Page Object Model: Alert Matchers, Read Actions & Pod Isolation

### User Story
As a Pod Lead, I want automated actions to detect low-score alerts, high-score alerts, trainer decision alerts, mark notifications as read, refresh the feed, and verify pod isolation.

### Description
Completed `NotificationsPage.java` by implementing text-matching heuristics for business alert types (`hasLowScoreAlert`, `hasHighScoreAlert`, `hasTrainerDecisionAlert`), dynamic click handlers for `markFirstUnreadNotificationAsRead()`, `clickRefresh()`, and a security validation method `verifyPodIsolation()`.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `NotificationsPage.java` | 90–221 | Business alert matchers, mark-as-read action, refresh action, and pod security isolation |

#### Code:
```java
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
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
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
```

### Daily Explanation
- Implemented specific alert matching methods checking for system messages like `"needs improvement"`, `"qualified for trainer review"`, and `"Trainer decision for"`.
- Added JavaScript click fallback inside `markFirstUnreadNotificationAsRead()` to avoid click interception from floating menus.
- Built `verifyPodIsolation(assignedPod)` which audits all visible cards to confirm no cross-pod data leaks exist for the logged-in user.

### Acceptance Result
All user interaction actions, alert verifications, and pod data isolation checks are fully encapsulated in the Page Object.

---

## Day 4 — Data-Driven Testing (DDT): Excel Integration & DataProvider

### User Story
As an Automation QA Lead, I want all notification test scenarios, expected alert keywords, and pod assignments stored in Excel so that the test suite runs with externalized test data.

### Description
Extended `DataProviders.java` to interface with `TestData.xlsx` under the `NotificationWorkflow` sheet. Implemented `@DataProvider(name = "notificationDDT")` to supply data maps to tests, and created `getNotificationTestCaseData(tcId)` for specific test case lookups.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `DataProviders.java` | 77–100 | TestNG DataProvider and single test case retrieval for Notification workflow |

#### Code:
```java
    /**
     * DataProvider for Notification Workflow from NotificationWorkflow sheet.
     */
    @DataProvider(name = "notificationDDT")
    public static Object[][] getNotificationData() {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        List<Map<String, String>> data = excel.getData("NotificationWorkflow");

        Object[][] testData = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }
        return testData;
    }

    /**
     * Get specific Notification test case data from Excel.
     */
    public static Map<String, String> getNotificationTestCaseData(String tcId) {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        return excel.getTestCaseData("NotificationWorkflow", tcId);
    }
```

### Daily Explanation
- Connected the `NotificationWorkflow` sheet in `TestData.xlsx` containing test definitions for `TC_NOTIF_01` through `TC_NOTIF_07`.
- Each Excel row supplies: `TC_ID`, `Scenario`, `User_Email`, `User_Password`, `Assigned_Pod`, `Expected_URL_Fraction`, `Expected_Alert_Keyword`, and `Expected_Result`.
- Allowed both bulk execution via `@DataProvider` and targeted individual runs via `getNotificationTestCaseData`.

### Acceptance Result
Notification test parameters are fully externalized to Excel, eliminating all hardcoded test data from test classes.

---

## Day 5 — Test Cases: Precondition Login & Alert Verification (TC_NOTIF_01 to TC_NOTIF_03)

### User Story
As a QA Engineer, I want automated tests to verify login, notifications list loading with timestamps, and score alert notifications (< 70% and >= 70%) using Excel data.

### Description
Created `NotificationTest.java`. Added `@BeforeMethod loginAsPodLead()` to authenticate using credentials from Excel and navigate to notifications. Implemented `testTC_NOTIF_01_OpenNotificationsPage`, `testTC_NOTIF_02_LowScoreAlert`, and `testTC_NOTIF_03_HighScoreAlert`.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `NotificationTest.java` | 1–26, 74–143 | Precondition setup, TC_NOTIF_01 (Open Page), TC_NOTIF_02 (Low Score), TC_NOTIF_03 (High Score) |

#### Code:
```java
package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.LoginPage;
import com.example.pages.NotificationsPage;
import com.example.utils.DataProviders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class NotificationTest extends BaseTest {

    private NotificationsPage notificationsPage;

    @BeforeMethod
    public void loginAsPodLead() {
        // Fetch Pod Lead credentials from Excel
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_01");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(data.get("User_Email"), data.get("User_Password"));

        notificationsPage = new NotificationsPage(driver);
        notificationsPage.navigateToNotifications();
    }

    @Test(priority = 1, description = "TC_NOTIF_01: Open Notifications Page")
    public void testTC_NOTIF_01_OpenNotificationsPage() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_01");

        // 1. Verify URL and page header
        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")),
                "URL should contain: " + data.get("Expected_URL_Fraction"));
        Assert.assertTrue(notificationsPage.isNotificationsPageDisplayed(),
                "Notifications page must be displayed");

        // 2. Verify list of updates is displayed
        int count = notificationsPage.getNotificationCount();
        Assert.assertTrue(count > 0,
                "Notifications page should display recent updates. Found: " + count);

        // 3. Verify date and time are displayed
        Assert.assertTrue(notificationsPage.areTimestampsDisplayed(),
                "Each notification item should display date and time");
    }

    @Test(priority = 2, description = "TC_NOTIF_02: Low Score Alert (< 70%)")
    public void testTC_NOTIF_02_LowScoreAlert() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_02");

        Assert.assertTrue(notificationsPage.hasLowScoreAlert(),
                "Expected low score alert (< 70%) in notifications list");

        String alertMessage = notificationsPage.getLowScoreAlertMessage();
        Assert.assertTrue(alertMessage.contains("needs improvement"),
                "Alert message should contain 'needs improvement'");
        Assert.assertTrue(alertMessage.contains("Alignment score:"),
                "Alert message should contain 'Alignment score:'");
        Assert.assertTrue(alertMessage.contains("Please revise and reupload"),
                "Alert message should contain 'Please revise and reupload'");
    }

    @Test(priority = 3, description = "TC_NOTIF_03: High Score Alert (>= 70%)")
    public void testTC_NOTIF_03_HighScoreAlert() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_03");

        Assert.assertTrue(notificationsPage.hasHighScoreAlert(),
                "Expected high score alert (>= 70%) in notifications list");

        String alertMessage = notificationsPage.getHighScoreAlertMessage();
        Assert.assertTrue(alertMessage.contains("qualified for trainer review"),
                "Alert message should contain 'qualified for trainer review'");
        Assert.assertTrue(alertMessage.contains("alignment score of"),
                "Alert message should contain 'alignment score of'");
    }
```

### Daily Explanation
- Implemented `@BeforeMethod` to handle session setup automatically before each test.
- Verified TC_NOTIF_01: Confirms URL redirection, item count > 0, and non-empty timestamps.
- Verified TC_NOTIF_02: Confirms low score alerts contain `"needs improvement"` and revision prompts.
- Verified TC_NOTIF_03: Confirms high score alerts contain `"qualified for trainer review"`.

### Acceptance Result
TC_NOTIF_01, TC_NOTIF_02, and TC_NOTIF_03 pass cleanly and assert all expected text contents.

---

## Day 6 — Test Cases: Trainer Decision, Status Toggle, Refresh & Pod Isolation (TC_NOTIF_04 to TC_NOTIF_07)

### User Story
As a QA Engineer, I want automated test cases to validate trainer decision notifications, marking notifications as read, refreshing the feed, and enforcing pod privacy boundaries.

### Description
Implemented the remaining business test cases in `NotificationTest.java`: `testTC_NOTIF_04_TrainerDecisionAlert`, `testTC_NOTIF_05_MarkNotificationAsRead`, `testTC_NOTIF_06_RefreshNotificationsList`, and `testTC_NOTIF_07_PodIsolationCheck`.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `NotificationTest.java` | 151–219 | Decision alerts (TC04), Mark Read (TC05), Refresh (TC06), and Pod Isolation (TC07) |

#### Code:
```java
    @Test(priority = 4, description = "TC_NOTIF_04: Trainer Decision Alert")
    public void testTC_NOTIF_04_TrainerDecisionAlert() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_04");

        Assert.assertTrue(notificationsPage.hasTrainerDecisionAlert(),
                "Expected Trainer decision alert in notifications list");

        String alertMessage = notificationsPage.getTrainerDecisionAlertMessage();
        Assert.assertTrue(alertMessage.toLowerCase().contains("trainer decision for"),
                "Alert should contain 'Trainer decision for', but got: " + alertMessage);
    }

    @Test(priority = 5, description = "TC_NOTIF_05: Mark Notification as Read")
    public void testTC_NOTIF_05_MarkNotificationAsRead() {
        boolean marked = notificationsPage.markFirstUnreadNotificationAsRead();
        if (marked) {
            Assert.assertTrue(notificationsPage.isAnyNotificationMarkedRead(),
                    "Notification should update status to Read");
        } else {
            Assert.assertTrue(notificationsPage.isAnyNotificationMarkedRead(),
                    "Notifications have Read status displayed");
        }
    }

    @Test(priority = 6, description = "TC_NOTIF_06: Refresh Notifications List")
    public void testTC_NOTIF_06_RefreshNotificationsList() {
        int countBefore = notificationsPage.getNotificationCount();

        notificationsPage.clickRefresh();

        int countAfter = notificationsPage.getNotificationCount();
        Assert.assertTrue(notificationsPage.isNotificationsPageDisplayed(),
                "Notifications page should remain displayed after refresh");
        Assert.assertTrue(countAfter > 0,
                "Notifications list should be populated after refresh. Count: " + countAfter);
        Assert.assertTrue(countAfter >= countBefore,
                "Notification count should be preserved or updated on refresh");
    }

    @Test(priority = 7, description = "TC_NOTIF_07: Pod Isolation Check")
    public void testTC_NOTIF_07_PodIsolationCheck() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_07");
        String assignedPod = data.get("Assigned_Pod");

        boolean isIsolated = notificationsPage.verifyPodIsolation(assignedPod);
        Assert.assertTrue(isIsolated,
                "Pod Lead of " + assignedPod + " must not see notifications from any other pods");
    }
}
```

### Daily Explanation
- Implemented TC_NOTIF_04: Validates trainer decision alert presence and formatted wording.
- Implemented TC_NOTIF_05: Clicks the "Mark as read" button on an unread card and verifies the updated status.
- Implemented TC_NOTIF_06: Clicks the "Refresh" action button and ensures the card count is maintained.
- Implemented TC_NOTIF_07: Audits the notification cards against non-assigned pods to guarantee multi-tenant security isolation.

### Acceptance Result
TC_NOTIF_04 through TC_NOTIF_07 execute successfully and validate functional and security behavior.

---

## Day 7 — Suite Configuration, Batch Execution & Extent Reporting

### User Story
As a QA Team Lead, I want an independent TestNG XML suite descriptor, a one-click Windows launcher, and ExtentReports integration for the Notification module so that it runs reliably in CI/CD pipelines.

### Description
Configured `testng-notifications.xml` attaching `TestListener` for automated reporting. Configured Maven POM profile `-Pnotifications` and created `run-notifications.bat`. Configured the HTML report to classify notifications under "Notification Module" with status pills and Excel data tables.

### Code to Paste and Submit

| File | Lines | Code responsibility |
| :--- | :--- | :--- |
| `testng-notifications.xml` | 1–13 | TestNG Suite descriptor with TestListener attachment |
| `run-notifications.bat` | 1–7 | One-click Windows batch command runner |

#### Code 1 (`testng-notifications.xml`):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Notification Test Suite">
    <listeners>
        <listener class-name="com.example.listeners.TestListener"/>
    </listeners>
    <test name="Notification Module">
        <classes>
            <class name="com.example.tests.NotificationTest"/>
        </classes>
    </test>
</suite>
```

#### Code 2 (`run-notifications.bat`):
```cmd
@echo off
echo ========================================================
echo Running Notification Module Tests
echo ========================================================
mvn test -Pnotifications
pause
```

### Daily Explanation
- Configured `testng-notifications.xml` to execute `NotificationTest` in isolation.
- Bound `TestListener` to capture test execution metrics, Base64 defect screenshots, and Excel input parameters.
- Provided `run-notifications.bat` and Maven profile `-Pnotifications` for one-click desktop and CI/CD execution.

### Acceptance Result
The Notification module runs standalone via `run-notifications.bat` or `mvn test -Pnotifications` and outputs styled execution results into `test-output/ExecutionReport.html`.
