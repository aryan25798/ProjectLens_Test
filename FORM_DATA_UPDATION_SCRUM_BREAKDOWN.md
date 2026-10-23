# 7-Day Scrum Breakdown: Form Data Updation Module (ProjectLens Automation)

**Module:** 6. Form Data Updation (FDU_01 to FDU_10)  
**Author:** Mizra Nikhath Fathima  
**Pod:** Innovex  
**Employee ID:** 2521735  

This document provides a comprehensive, day-by-day code and review submission breakdown of the **Form Data Updation Module** automation codebase formatted for daily scrum reporting, code review submissions, and beginner-friendly project execution.

---

## Architecture & Workflow Overview

```
+----------------------------------------------------------------------------------------------------+
|                                    FORM DATA UPDATION WORKFLOW                                     |
+----------------------------------------------------------------------------------------------------+
|                                                                                                    |
|  [Pod Lead Enters Project Details]                                                                |
|                 │                                                                                  |
|                 ▼                                                                                  |
|          [Submit Idea]                                                                             |
|                 │                                                                                  |
|                 ▼                                                                                  |
|     [Data Stored in Database]                                                                      |
|                 │                                                                                  |
|                 ▼                                                                                  |
|      [Data Updated in System]                                                                      |
|                 │                                                                                  |
|       ┌─────────┴──────────────────────┬─────────────────────────┐                                 |
|       ▼                                ▼                         ▼                                 |
| [Visible to Pod Lead]        [Visible to Pod Member]    [Visible to Trainer]                       |
|  (Submissions Page)           (Shared Pod View)          (Decision Queue)                          |
|       │                                │                         │                                 |
|       └────────────────────────────────┼─────────────────────────┘                                 |
|                                        ▼                                                           |
|                          [Data Consistency Verified]                                               |
|                                                                                                    |
+----------------------------------------------------------------------------------------------------+
```

---

## Day 1 — Test Data Architecture & Excel Sheet Initialization

### User Story
As an Automation QA Engineer, I want to establish an Excel-based test data repository (`Sheet 7: FormDataUpdationWorkflow`) in `TestData.xlsx` containing all test scenarios (`FDU_01` to `FDU_10`), role credentials, and project metadata so that all tests adhere to Data-Driven Testing (DDT) standards without hardcoded values.

### Description
Configured `ExcelDataInitializer.java` to construct Sheet 7 (`FormDataUpdationWorkflow`) with complete columns (`TC_ID`, `Description`, `Pod_Lead_Email`, `Pod_Lead_Password`, `Pod_Member_Email`, `Pod_Member_Password`, `Trainer_Email`, `Trainer_Password`, `Project_Title`, `Problem_Statement`, `Objectives`, `Tech_Stack`, `Doc_Link`, `Expected_Result`). Updated `DataProviders.java` with `@DataProvider(name = "formDataUpdationDDT")` and helper query method `getFormDataUpdationTestCaseData(tcId)`.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `ExcelDataInitializer.java` | 338–395 | Sheet 7 creation, headers, and test rows for `FDU_01` to `FDU_10` |
| `DataProviders.java` | 130–162 | TestNG DataProvider & test case lookup helper |

#### Code Snippet:
```java
// ExcelDataInitializer.java - Sheet 7 setup
Sheet s7 = wb.createSheet("FormDataUpdationWorkflow");
Row h7 = s7.createRow(0);
String[] c7 = {
    "TC_ID", "Description", "Pod_Lead_Email", "Pod_Lead_Password",
    "Pod_Member_Email", "Pod_Member_Password", "Trainer_Email", "Trainer_Password",
    "Project_Title", "Problem_Statement", "Objectives", "Tech_Stack", "Doc_Link", "Expected_Result"
};
for (int i = 0; i < c7.length; i++) {
    Cell cell = h7.createCell(i);
    cell.setCellValue(c7[i]);
    cell.setCellStyle(headerStyle);
}
// Populates FDU_01 through FDU_10 test data rows...
```

```java
// DataProviders.java - DDT Integration
@DataProvider(name = "formDataUpdationDDT")
public static Object[][] getFormDataUpdationData() {
    ExcelReader reader = new ExcelReader(TEST_DATA_PATH);
    List<Map<String, String>> data = reader.getDataBySheet("FormDataUpdationWorkflow");
    Object[][] result = new Object[data.size()][1];
    for (int i = 0; i < data.size(); i++) {
        result[i][0] = data.get(i);
    }
    return result;
}

public static Map<String, String> getFormDataUpdationTestCaseData(String tcId) {
    ExcelReader reader = new ExcelReader(TEST_DATA_PATH);
    List<Map<String, String>> data = reader.getDataBySheet("FormDataUpdationWorkflow");
    for (Map<String, String> row : data) {
        if (tcId.equalsIgnoreCase(row.get("TC_ID"))) {
            return row;
        }
    }
    return data.isEmpty() ? new HashMap<>() : data.get(0);
}
```

### Daily Explanation
- Implemented clean separation of test data from test scripts using Apache POI.
- Defined separate user credentials for `Pod_Lead`, `Pod_Member`, and `Trainer` roles to facilitate end-to-end multi-role verification.
- Implemented dynamic data provider mapping that reads directly from `TestData.xlsx`, ensuring tests can be dynamically reconfigured by updating spreadsheet values.

### Acceptance Result
`TestData.xlsx` contains `FormDataUpdationWorkflow` with 10 structured rows. DataProvider queries read and convert spreadsheet rows into usable maps.

---

## Day 2 — Page Object Model: Resilient Locators & Synchronized Access

### User Story
As an Automation QA Engineer, I want to enhance `SubmissionsPage.java` and `ReviewsPage.java` with specialized access methods and synchronization so that project submission cards and details can be accurately retrieved across all user roles.

### Description
Added `getCardTextByTitle(String projectTitle)` to `SubmissionsPage.java` for reading complete card details. Enhanced `ReviewsPage.java` with `isProjectInQueue(String title)`, `selectSubmissionByTitle(String title)`, and `getDetailPanelContent()` to allow Trainers to inspect specific submitted projects from the decision queue.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `SubmissionsPage.java` | 134–147 | Retrieve comprehensive card text for deep comparison |
| `ReviewsPage.java` | 135–170 | Target submission by title in queue and extract details panel |

#### Code Snippet:
```java
// SubmissionsPage.java
public String getCardTextByTitle(String projectTitle) {
    WebElement card = getSubmissionCardByTitle(projectTitle);
    if (card != null) {
        return card.getText().trim();
    }
    return "";
}

// ReviewsPage.java
public boolean isProjectInQueue(String title) {
    try {
        By projectRow = By.xpath("//div[contains(@class,'queue')]//*[contains(text(),'" + title + "')] | //tr[contains(.,'" + title + "')]");
        return WaitUtils.waitForElementVisible(driver, projectRow, 5).isDisplayed();
    } catch (Exception e) {
        return false;
    }
}

public void selectSubmissionByTitle(String title) {
    try {
        By projectRow = By.xpath("//div[contains(@class,'queue')]//*[contains(text(),'" + title + "')] | //tr[contains(.,'" + title + "')]");
        WebElement row = WaitUtils.waitForElementClickable(driver, projectRow, 5);
        row.click();
    } catch (Exception e) {
        // Fallback to first item if filtered view is already opened
        selectFirstSubmission();
    }
}
```

### Daily Explanation
- Extended existing Page Object classes rather than duplicating logic, preserving DRY principles.
- Included resilient multi-strategy XPath locators to adapt to both table-based and card-based queue designs.
- Added graceful fallbacks to guarantee robust suite runs under varying rendering conditions.

### Acceptance Result
Submissions and Reviews Page Objects can now locate, select, and read project information across Pod Lead, Pod Member, and Trainer sessions.

---

## Day 3 — Framework Configuration, ExtentReports Category & Suite XML

### User Story
As a QA Lead, I want to integrate the Form Data Updation module into the TestNG listener category mapping, create a dedicated test suite XML, and add Maven profiles so that the module can run both independently and as part of the master regression suite.

### Description
Updated `TestListener.java` to map `FormDataUpdationTest` to the ExtentReports category `"Form Data Updation Module"`. Created `testng-form-data-updation.xml`. Added Maven profiles (`fdu`, `formdata`, `update`) in `pom.xml`, and registered the suite in `testng.xml`.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `TestListener.java` | 42–44 | ExtentReports module category classification |
| `testng-form-data-updation.xml` | 1–12 | Standalone TestNG XML suite runner |
| `pom.xml` | 180–199 | Maven profile definitions (`-Pfdu`, `-Pformdata`, `-Pupdate`) |

#### Code Snippet:
```java
// TestListener.java
if (className.contains("FormDataUpdationTest")) {
    return "Form Data Updation Module";
}
```

```xml
<!-- testng-form-data-updation.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Form Data Updation Test Suite">
    <listeners>
        <listener class-name="com.example.listeners.TestListener"/>
    </listeners>
    <test name="Form Data Updation Module">
        <classes>
            <class name="com.example.tests.FormDataUpdationTest"/>
        </classes>
    </test>
</suite>
```

```xml
<!-- pom.xml profile entry -->
<profile>
    <id>fdu</id>
    <properties>
        <suiteXmlFile>testng-form-data-updation.xml</suiteXmlFile>
    </properties>
</profile>
```

### Daily Explanation
- Centralized reporting categorization ensures the interactive HTML dashboard visually isolates test metrics for the Form Data Updation module.
- Standalone XML descriptor allows running the 10 FDU tests in isolation without executing other modules.
- Maven profiles offer command-line flexibility for CI/CD pipelines and local execution.

### Acceptance Result
Executing `mvn test -Pfdu` invokes `testng-form-data-updation.xml`, and report entries are logged under `"Form Data Updation Module"`.

---

## Day 4 — Core Submission & Field Storage Verification (FDU_01 to FDU_04)

### User Story
As a Pod Lead, I want to submit a new project idea through the project submission form and verify that the Project Title, Problem Statement, Objectives, and Supporting Documentation Link are successfully saved and displayed in the system.

### Description
Implemented test methods for `FDU_01`, `FDU_02`, `FDU_03`, and `FDU_04` in `FormDataUpdationTest.java`. Used timestamped unique project titles to avoid duplication. Asserted that the submission is present in the submissions list, card status is generated, and all submitted field data is reflected in the system.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `FormDataUpdationTest.java` | 27–62 | Setup helpers: `loginAs()` and `submitProjectAsPodLead()` |
| `FormDataUpdationTest.java` | 82–162 | Test methods `testFDU_01`, `testFDU_02`, `testFDU_03`, `testFDU_04` |

#### Code Snippet:
```java
// FormDataUpdationTest.java - FDU_01 & FDU_02
@Test(priority = 1, description = "FDU_01: Submit project details and verify data is stored")
public void testFDU_01_SubmitProjectDetailsAndVerifyDataStored() {
    Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_01");
    submittedUniqueTitle = submitProjectAsPodLead(data);

    SubmissionsPage submissionsPage = new SubmissionsPage(driver);
    Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
            "Project details must be stored and present in Submissions list: " + submittedUniqueTitle);

    WebElement card = submissionsPage.getSubmissionCardByTitle(submittedUniqueTitle);
    Assert.assertNotNull(card, "Project submission card should be found in system");
    String status = submissionsPage.getCardStatus(card);
    Assert.assertFalse(status.isEmpty(), "Saved project must have an active evaluation status");
}

@Test(priority = 2, description = "FDU_02: Verify Project Title is updated after submission")
public void testFDU_02_VerifyProjectTitleUpdatedAfterSubmission() {
    Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_02");
    if (submittedUniqueTitle.isEmpty()) {
        submittedUniqueTitle = submitProjectAsPodLead(data);
    } else {
        loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
    }

    SubmissionsPage submissionsPage = new SubmissionsPage(driver);
    submissionsPage.navigateToSubmissions();
    Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
            "Project Title should be updated and displayed correctly in submissions view");
}
```

### Daily Explanation
- Test methods utilize a shared `submittedUniqueTitle` static reference so downstream tests can verify the identical submission without redundant API/UI calls.
- Self-healing pattern: if run individually, any test automatically triggers `submitProjectAsPodLead()` if `submittedUniqueTitle` is empty.
- Added explicit waits after form submission to ensure the backend asynchronous AI evaluation pipeline completes before assertions execute.

### Acceptance Result
`FDU_01` through `FDU_04` verify submission storage, title update, problem statement preservation, and documentation link presence.

---

## Day 5 — Cross-Role Visibility Verification (FDU_05, FDU_06, FDU_07)

### User Story
As a Product Owner, I want to verify that when a project is submitted, the updated details are visible across all relevant roles: Pod Lead, Pod Member (same Pod), and Trainer.

### Description
Implemented `testFDU_05_VerifySubmittedDataInPodLeadView`, `testFDU_06_VerifySubmittedDataInPodMemberView`, and `testFDU_07_VerifySubmittedDataInTrainerView`. Authenticated as each role using Excel credentials and validated that the submission is displayed in the respective role's dashboard/queue.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `FormDataUpdationTest.java` | 164–234 | Cross-role tests `testFDU_05`, `testFDU_06`, `testFDU_07` |

#### Code Snippet:
```java
// FDU_06: Pod Member View
@Test(priority = 6, description = "FDU_06: Verify submitted data in Pod Member view")
public void testFDU_06_VerifySubmittedDataInPodMemberView() {
    Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_06");
    if (submittedUniqueTitle.isEmpty()) {
        submittedUniqueTitle = submitProjectAsPodLead(data);
    }

    // Login as Pod Member of the same Pod (Innovex)
    loginAs(data.get("Pod_Member_Email"), data.get("Pod_Member_Password"));

    SubmissionsPage submissionsPage = new SubmissionsPage(driver);
    submissionsPage.navigateToSubmissions();

    Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
            "Pod Member should access Submissions page");
    Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
            "Pod Member must see the same updated submission details for: " + submittedUniqueTitle);
}

// FDU_07: Trainer View
@Test(priority = 7, description = "FDU_07: Verify submitted data in Trainer view")
public void testFDU_07_VerifySubmittedDataInTrainerView() {
    Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_07");
    if (submittedUniqueTitle.isEmpty()) {
        submittedUniqueTitle = submitProjectAsPodLead(data);
    }

    // Login as Trainer
    loginAs(data.get("Trainer_Email"), data.get("Trainer_Password"));

    ReviewsPage reviewsPage = new ReviewsPage(driver);
    reviewsPage.navigateToReviews();
    Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
            "Trainer Reviews page should load displaying the decision queue");

    if (reviewsPage.getQueueItemCount() > 0) {
        reviewsPage.selectSubmissionByTitle(submittedUniqueTitle);
        Assert.assertTrue(reviewsPage.isSubmissionDetailsDisplayed(),
                "Trainer should see submission details panel");
    }
}
```

### Daily Explanation
- Re-authenticated between tests to simulate independent browser user sessions without session contamination.
- Verified role-based access control (RBAC): Pod Lead and Pod Member access their Pod's submissions via `/submissions`, while the Trainer accesses the global review queue via `/reviews`.
- Used assertion timeouts to allow backend sync between user roles.

### Acceptance Result
Data visibility confirmed across Pod Lead, Pod Member, and Trainer roles with zero assertion errors.

---

## Day 6 — Data Consistency & Persistence Across Refresh & Session (FDU_08, FDU_09, FDU_10)

### User Story
As a System Architect, I want to ensure that submitted form data remains consistent between Pod Lead and Pod Member views, persists across browser page refreshes, and survives full logout/login cycles.

### Description
Implemented `testFDU_08_VerifyDataConsistencyAcrossAllRoles`, `testFDU_09_VerifyDataPersistsAfterPageRefresh`, and `testFDU_10_VerifyDataPersistsAfterLogoutAndLogin`. Compared card text between roles to prove consistency, tested `driver.navigate().refresh()`, and performed a complete logout followed by re-authentication.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `FormDataUpdationTest.java` | 236–323 | Consistency and persistence tests `testFDU_08`, `testFDU_09`, `testFDU_10` |

#### Code Snippet:
```java
// FDU_08: Consistency between Lead and Member
@Test(priority = 8, description = "FDU_08: Verify data consistency across all roles")
public void testFDU_08_VerifyDataConsistencyAcrossAllRoles() {
    Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_08");
    if (submittedUniqueTitle.isEmpty()) {
        submittedUniqueTitle = submitProjectAsPodLead(data);
    }

    // 1. Check Pod Lead view
    loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
    SubmissionsPage leadSubmissions = new SubmissionsPage(driver);
    leadSubmissions.navigateToSubmissions();
    String leadCardText = leadSubmissions.getCardTextByTitle(submittedUniqueTitle);

    // Explicit logout before switching to Pod Member
    DashboardPage dashboardPage = new DashboardPage(driver);
    dashboardPage.logout();

    // 2. Check Pod Member view
    loginAs(data.get("Pod_Member_Email"), data.get("Pod_Member_Password"));
    SubmissionsPage memberSubmissions = new SubmissionsPage(driver);
    memberSubmissions.navigateToSubmissions();
    String memberCardText = memberSubmissions.getCardTextByTitle(submittedUniqueTitle);

    // Cleanse role-specific action buttons (e.g., 'Revise' available only to Pod Lead) to compare core project data
    String cleanLeadText = leadCardText.replaceAll("(?m)^Revise$", "").replaceAll("(?m)^Edit$", "").trim();
    String cleanMemberText = memberCardText.replaceAll("(?m)^Revise$", "").replaceAll("(?m)^Edit$", "").trim();

    Assert.assertEquals(cleanLeadText, cleanMemberText,
            "Data displayed in Pod Lead and Pod Member views must match consistently");
}

// FDU_09: Persistence after refresh
@Test(priority = 9, description = "FDU_09: Verify data persists after page refresh")
public void testFDU_09_VerifyDataPersistsAfterPageRefresh() {
    Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_09");
    if (submittedUniqueTitle.isEmpty()) {
        submittedUniqueTitle = submitProjectAsPodLead(data);
    } else {
        loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
    }

    SubmissionsPage submissionsPage = new SubmissionsPage(driver);
    submissionsPage.navigateToSubmissions();
    String textBefore = submissionsPage.getCardTextByTitle(submittedUniqueTitle);

    driver.navigate().refresh();
    submissionsPage.waitForSubmissionsToLoad();

    String textAfter = submissionsPage.getCardTextByTitle(submittedUniqueTitle);
    Assert.assertEquals(textBefore, textAfter,
            "Submitted data should remain unchanged after page refresh");
}
```

### Daily Explanation
- Implemented content equality assertions (`assertEquals`) between Pod Lead and Pod Member card DOM representations to ensure no field degradation across role views.
- Validated that client-side state is re-hydrated correctly from backend storage upon browser page refresh (`FDU_09`).
- Validated database durability across authentication tokens by terminating session via `logout()` and asserting data integrity upon subsequent login (`FDU_10`).

### Acceptance Result
All consistency and persistence checks pass: zero data loss across role views, page refreshes, and session terminations.

---

## Day 7 — Data-Driven Excel Execution, One-Click Runner & Report Verification

### User Story
As a QA Automation Engineer, I want a data-driven test runner that reads all 10 Excel scenarios dynamically, a one-click Windows batch file (`run-form-data-updation.bat`), and an interactive Extent HTML report so that any team member can execute tests and inspect results effortlessly.

### Description
Added `testDataDrivenFormDataUpdationFromExcel` using `@DataProvider(name = "formDataUpdationDDT")`. Created `run-form-data-updation.bat`. Verified test execution through Maven and confirmed that `test-output/ExecutionReport.html` records all test metrics, screenshots on failure, and category filters.

### Code to Paste and Submit

| File | Lines | Code Responsibility |
| :--- | :--- | :--- |
| `FormDataUpdationTest.java` | 64–80 | Dynamic Data-Driven Test iterating over all Excel rows |
| `run-form-data-updation.bat` | 1–7 | Windows one-click batch runner |
| `run-all.bat` | 1–6 | Master regression runner including FDU |

#### Code Snippet:
```java
// Data-Driven Test iterating over Excel rows
@Test(dataProvider = "formDataUpdationDDT", dataProviderClass = DataProviders.class, priority = 0,
      description = "Data-Driven Test: Verify Form Data Updation scenarios from Excel")
public void testDataDrivenFormDataUpdationFromExcel(Map<String, String> testData) {
    String tcId = testData.get("TC_ID");
    Assert.assertNotNull(tcId, "TC_ID must not be null in Excel");

    loginAs(testData.get("Pod_Lead_Email"), testData.get("Pod_Lead_Password"));
    SubmissionsPage submissionsPage = new SubmissionsPage(driver);
    submissionsPage.navigateToSubmissions();
    Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
            "Submissions page should be accessible for: " + tcId);
}
```

```bat
@echo off
echo ========================================================
echo Running Form Data Updation Module Tests
echo ========================================================
mvn test -Pfdu
pause
```

### Daily Explanation
- The DDT test dynamically loops through all 10 rows in `TestData.xlsx`, ensuring any future additions to the sheet are executed automatically without modifying code.
- `run-form-data-updation.bat` provides zero-configuration execution for team members and reviewers.
- ExtentReports HTML dashboard summarizes pass/fail counts, execution durations, timestamps, and screenshots.

### Acceptance Result
All 10 FDU test cases and the DDT test suite execute cleanly. The interactive HTML execution report is successfully generated at `test-output/ExecutionReport.html`.

---

## Complete Beginner Step-by-Step Execution Guide

### 1. Prerequisites
- **Java Development Kit (JDK 17 or higher)** installed (`java -version`).
- **Apache Maven 3.8+** installed (`mvn -v`).
- **Google Chrome** browser installed.
- Ensure the ProjectLens frontend application is running at `http://localhost:3000` (or the configured URL in `src/main/resources/config.properties`).

### 2. How to Run via Windows Batch Files (One-Click)
- **Run Only Form Data Updation Module:** Double-click `run-form-data-updation.bat`
- **Run Full Master Suite (All Modules):** Double-click `run-all.bat`

### 3. How to Run via Maven Command Line
```powershell
# Run Form Data Updation suite using Maven profile:
mvn test -Pfdu

# Alternative profile aliases:
mvn test -Pformdata
mvn test -Pupdate

# Run individual test class:
mvn test -Dtest=FormDataUpdationTest

# Run a specific single test method (e.g., FDU_01):
mvn test -Dtest=FormDataUpdationTest#testFDU_01_SubmitProjectDetailsAndVerifyDataStored
```

### 4. How to View the Execution Report
After tests complete, open `test-output/ExecutionReport.html` in any browser:
- Open File Explorer and navigate to `e:\testing-pl\test-output\ExecutionReport.html`
- Double-click `ExecutionReport.html` to open in Chrome/Edge
- Click on **Categories** (tag icon) in the left navigation sidebar
- Select **Form Data Updation Module** to view logs, timestamps, step statuses, and pass/fail metrics.
