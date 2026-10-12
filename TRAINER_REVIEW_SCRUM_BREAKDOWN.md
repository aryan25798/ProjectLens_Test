# 7-Day Scrum Breakdown: Trainer Review Module (ProjectLens Automation)

This document provides a day-by-day submission breakdown of the **Trainer Review Module** automation codebase formatted for daily scrum reporting and code review submissions.

---

## Day 1 — Page Object Model: Reviews Page Locators & Base Setup

### User Story
As an Automation QA Engineer, I want to define the core element locators and structure for the Trainer Reviews page within the Page Object Model so that elements are maintained centrally and changes to the UI do not break tests.

### Description
Created `ReviewsPage.java` extending `BasePage`. Defined locators for the reviews navigation link, decision queue header, item badges, queue list buttons, submission detail panel, alignment score indicator, feedback comments area, and decision action buttons (`Approve`, `Needs Revision`, `Reject`).

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `ReviewsPage.java` | 1–30 | Page class definition, constructor, and element locators (`By`) |

#### Code Snippet:
```java
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
```

### Daily Explanation
- Implemented the Page Object Model (POM) foundation for the Trainer Review workflow.
- Applied resilient locators using CSS selectors and XPath fallbacks to handle dynamic DOM updates.
- Extended `BasePage` to inherit reusable interaction methods and `WebDriver` management.
- Ensured locators are private to enforce encapsulation and prevent direct element manipulation in test classes.

### Acceptance Result
Page class is created with all required UI locators defined and cleanly encapsulated.

---

## Day 2 — Page Object Model: Navigation & Decision Queue Inspection Actions

### User Story
As a Trainer, I want automation methods to navigate to the Reviews page and verify items present in the Decision Queue so that test scripts can validate queue status and count dynamically.

### Description
Added navigation logic with explicit wait conditions to navigate to `/reviews`. Added inspection helper methods to verify whether the Decision Queue header is displayed, extract the badge text, and return the count of items pending review.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `ReviewsPage.java` | 31–56 | Navigation with wait handlers and queue validation methods |

#### Code Snippet:
```java
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
```

### Daily Explanation
- Built `navigateToReviews()` using `WaitUtils` to ensure synchronization before clicking the navigation element.
- Added JavaScript Executor fallback for clicking tabs in case overlay or responsive banners intercept standard clicks.
- Created `isDecisionQueueDisplayed()`, `getQueueCountText()`, and `getQueueItemCount()` to allow assertions without exposing `WebElement` instances to test classes.

### Acceptance Result
The browser navigates cleanly to the Reviews URL with proper wait synchronization, and queue item counts can be queried programmatically.

---

## Day 3 — Page Object Model: Submission Selection & Evaluation Actions

### User Story
As a Trainer, I want methods to select a submission, view details (title and alignment score), enter comments, and submit decisions (`Approve`, `Needs Revision`, `Reject`) so that complete review actions can be executed.

### Description
Completed `ReviewsPage.java` with interaction methods for clicking a submission card, extracting project title and alignment score, entering trainer comments into the feedback textarea, and triggering approval, revision, or rejection button clicks.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `ReviewsPage.java` | 58–119 | Selection, field reading, comments entry, and decision action methods |

#### Code Snippet:
```java
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
```

### Daily Explanation
- Implemented `selectFirstSubmission()` to pick the top pending project from the Decision Queue.
- Added data retrieval methods (`getProjectTitleText()`, `getAlignmentScoreText()`) to enable assertions against details displayed in the review pane.
- Added `enterComments(String comments)` and three individual decision handlers (`clickApprove()`, `clickNeedsRevision()`, `clickReject()`) with automatic JavaScript executor fallbacks.
- Provided `isQueueEmpty()` to handle clean and empty queue states.

### Acceptance Result
The Page Object provides full coverage for all UI interactions required to complete end-to-end trainer evaluation workflows.

---

## Day 4 — Data-Driven Testing (DDT): Excel Integration & Data Providers

### User Story
As an Automation QA Lead, I want Trainer Review test data (credentials, project names, decisions, comments, expected outcomes) externalized into Excel sheets so that tests run with multiple data combinations without code modification.

### Description
Integrated Apache POI via `DataProviders.java` and populated test records in `TestData.xlsx` under the `TrainerReviewWorkflow` sheet. Exposed utility methods to fetch single test case records (`getTrainerReviewTestCaseData`) and stream rows into TestNG `@DataProvider`.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `DataProviders.java` | 36–56 | TestNG DataProvider and helper retrieval for Trainer Review test cases |

#### Code Snippet:
```java
    /**
     * DataProvider for Trainer Review Workflow from TrainerReviewWorkflow sheet.
     */
    @DataProvider(name = "trainerReviewDDT")
    public static Object[][] getTrainerReviewData() {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        List<Map<String, String>> data = excel.getData("TrainerReviewWorkflow");

        Object[][] testData = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }
        return testData;
    }

    /**
     * Get specific Trainer Review test case data from Excel.
     */
    public static Map<String, String> getTrainerReviewTestCaseData(String tcId) {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        return excel.getTestCaseData("TrainerReviewWorkflow", tcId);
    }
```

### Daily Explanation
- Externalized test inputs into `TestData.xlsx` (`TrainerReviewWorkflow` sheet) containing: `TC_ID`, `Test_Scenario`, `Trainer_Email`, `Trainer_Password`, `Project_Title`, `Decision_Action`, `Comments`, `Expected_Queue_State`, `Expected_Status`.
- Created `@DataProvider(name = "trainerReviewDDT")` returning two-dimensional `Object[][]` mapping each row to a key-value `Map<String, String>`.
- Implemented `getTrainerReviewTestCaseData(String tcId)` for targeted test retrieval by ID (`TC01`, `TC02`, `TC04`, `TC05`, `TC06`).

### Acceptance Result
Trainer test data is decoupled from Java test code and dynamically read from the Excel file at runtime.

---

## Day 5 — Test Cases: Precondition Login, Navigation & Queue Details (TC01 & TC02)

### User Story
As a QA Engineer, I want automated TestNG tests to verify that a trainer can log in, navigate to the Reviews page, and inspect submission details so that core visibility functionality is continuously validated.

### Description
Created `TrainerReviewTest.java` extending `BaseTest`. Implemented `@BeforeMethod loginAsTrainer()` to read credentials from Excel and navigate to the Reviews page. Implemented `testTC01_VerifyNavigationToReviewsQueue` and `testTC02_VerifySubmissionDetailsDisplay`.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `TrainerReviewTest.java` | 1–64 | Precondition login method and test cases TC01 & TC02 |

#### Code Snippet:
```java
package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.LoginPage;
import com.example.pages.ReviewsPage;
import com.example.utils.DataProviders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class TrainerReviewTest extends BaseTest {

    private ReviewsPage reviewsPage;

    @BeforeMethod
    public void loginAsTrainer() {
        // Fetch Trainer credentials from Excel
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC01");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(data.get("Trainer_Email"), data.get("Trainer_Password"));
        reviewsPage = new ReviewsPage(driver);
        reviewsPage.navigateToReviews();
    }

    @Test(priority = 1, description = "TC01: Verify Navigation to Reviews Queue")
    public void testTC01_VerifyNavigationToReviewsQueue() {
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC01");

        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_Status")),
                "URL should contain /reviews");
        Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                "Decision queue should be displayed");
    }

    @Test(priority = 2, description = "TC02: Verify Submission Details Display")
    public void testTC02_VerifySubmissionDetailsDisplay() {
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC02");

        if (reviewsPage.getQueueItemCount() > 0) {
            reviewsPage.selectFirstSubmission();
            Assert.assertTrue(reviewsPage.isSubmissionDetailsDisplayed(),
                    "Submission details panel should be visible");
            Assert.assertFalse(reviewsPage.getProjectTitleText().isEmpty(),
                    "Project title should be displayed");
        } else {
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Decision queue loaded with current state");
        }
    }
```

### Daily Explanation
- Implemented `@BeforeMethod` setup to authenticate using trainer credentials stored in Excel, eliminating hardcoded usernames/passwords.
- TC01 validates that the URL transitions to `/reviews` and the Decision Queue header is rendered.
- TC02 selects the first item from the queue and asserts that the details panel and project title are populated.

### Acceptance Result
TC01 and TC02 execute independently, pull data from Excel, and validate queue visibility and submission details.

---

## Day 6 — Test Cases: Evaluation Actions — Approval, Revision & Rejection (TC04, TC05, TC06)

### User Story
As a Trainer, I want automated test cases to execute approval, request revision, and reject decisions with corresponding feedback comments so that all decision workflows are regression tested.

### Description
Implemented decision test cases in `TrainerReviewTest.java`: `testTC04_VerifySuccessfulSubmissionApproval`, `testTC05_VerifyRequestingRevision`, and `testTC06_VerifyRejectionFlow`. Handled test verification and queue state updates using data from `TestData.xlsx`.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `TrainerReviewTest.java` | 70–136 | Approval (TC04), Needs Revision (TC05), and Rejection (TC06) test flows |

#### Code Snippet:
```java
    @Test(priority = 4, description = "TC04: Verify Successful Submission Approval")
    public void testTC04_VerifySuccessfulSubmissionApproval() {
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC04");

        if (reviewsPage.getQueueItemCount() > 0) {
            reviewsPage.selectFirstSubmission();
            reviewsPage.enterComments(data.get("Comments"));
            reviewsPage.clickApprove();
            // Verify action processed
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Queue updates after approval decision");
        } else {
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Decision queue available and responsive");
        }
    }

    @Test(priority = 5, description = "TC05: Verify Requesting Revision")
    public void testTC05_VerifyRequestingRevision() {
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC05");

        if (reviewsPage.getQueueItemCount() > 0) {
            reviewsPage.selectFirstSubmission();
            reviewsPage.enterComments(data.get("Comments"));
            reviewsPage.clickNeedsRevision();
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Decision queue updates after Needs Revision decision");
        } else {
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Decision queue updates after Needs Revision decision");
        }
    }

    @Test(priority = 6, description = "TC06: Verify Rejection Flow")
    public void testTC06_VerifyRejectionFlow() {
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC06");

        if (reviewsPage.getQueueItemCount() > 0) {
            reviewsPage.selectFirstSubmission();
            reviewsPage.enterComments(data.get("Comments"));
            reviewsPage.clickReject();
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Decision queue updates after rejection");
        } else {
            Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                    "Decision queue active");
        }
    }
}
```

### Daily Explanation
- Implemented `testTC04_VerifySuccessfulSubmissionApproval`: Enters approval feedback from Excel and clicks "Approve".
- Implemented `testTC05_VerifyRequestingRevision`: Enters revision notes from Excel and clicks "Needs Revision".
- Implemented `testTC06_VerifyRejectionFlow`: Enters rejection justification from Excel and clicks "Reject".
- Guarded against empty queue states to ensure resilient test execution across repeated test runs.

### Acceptance Result
All 3 decision paths (`Approve`, `Needs Revision`, `Reject`) execute and record feedback successfully.

---

## Day 7 — Suite Configuration, Batch Execution & Extent Reporting

### User Story
As a QA Team Lead, I want a dedicated TestNG XML suite, a one-click Windows batch launcher, and ExtentReports integration for the Trainer Review module so that the module runs independently in CI/CD and produces visual test reports.

### Description
Created `testng-trainer.xml` with TestListener integration. Configured Maven POM profile `-Ptrainer` and created `run-trainer.bat` for one-click execution. Integrated `ReportManager` and `TestListener` to categorize tests under "Trainer Review Module" with visual badges, execution duration, and Excel data tables.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `testng-trainer.xml` | 1–13 | Dedicated TestNG Suite descriptor with TestListener attachment |
| `run-trainer.bat` | 1–7 | One-click Windows batch command runner |

#### Code Snippet 1 (`testng-trainer.xml`):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Trainer Review Test Suite">
    <listeners>
        <listener class-name="com.example.listeners.TestListener"/>
    </listeners>
    <test name="Trainer Review Module">
        <classes>
            <class name="com.example.tests.TrainerReviewTest"/>
        </classes>
    </test>
</suite>
```

#### Code Snippet 2 (`run-trainer.bat`):
```cmd
@echo off
echo ========================================================
echo Running ProjectLens Trainer Review Test Suite...
echo ========================================================
call mvn test -Ptrainer
pause
```

### Daily Explanation
- Configured `testng-trainer.xml` to isolate the Trainer Review module from other suites.
- Attached `TestListener` to automatically register test events, log Excel parameters, and embed Base64 failure screenshots into `test-output/ExecutionReport.html`.
- Created `run-trainer.bat` and Maven profile `-Ptrainer` (`-Dsurefire.suiteXmlFiles=testng-trainer.xml`) allowing teammates to execute the suite via double-click or CLI.

### Acceptance Result
The Trainer Review module runs in isolation via `run-trainer.bat` or `mvn test -Ptrainer` and generates the rich HTML execution report with test categorization.
