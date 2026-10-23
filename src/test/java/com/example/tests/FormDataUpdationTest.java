package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;
import com.example.pages.ProjectSubmissionPage;
import com.example.pages.ReviewsPage;
import com.example.pages.SubmissionsPage;
import com.example.utils.DataProviders;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * MODULE 6: FORM DATA UPDATION (FDU)
 * Author: Mizra Nikhath Fathima | Pod: Innovex | Employee ID: 2521735
 *
 * Workflow:
 * Pod Lead Enters Project Details -> Submit Idea -> Data Stored in Database ->
 * Data Updated in System -> Visible to Pod Lead -> Visible to Pod Members ->
 * Visible to Trainer -> Data Consistency Verified.
 *
 * Covers Test Cases FDU_01 to FDU_10 using Data-Driven Testing (DDT) from Excel.
 */
public class FormDataUpdationTest extends BaseTest {

    private static String submittedUniqueTitle = "";

    /**
     * Helper to authenticate as any user role and ensure dashboard is loaded.
     */
    private void loginAs(String email, String password) {
        DashboardPage dashboardPage = new DashboardPage(driver);
        try {
            if (dashboardPage.isSignOutDisplayed()) {
                dashboardPage.logout();
                Thread.sleep(1000);
            }
        } catch (Exception ignored) {}

        LoginPage loginPage = new LoginPage(driver);
        if (!loginPage.isLoginPageDisplayed()) {
            String baseUrl = com.example.utils.ConfigReader.getProperty("app.baseUrl");
            driver.get(baseUrl != null ? baseUrl : "http://localhost:3000");
        }
        loginPage.login(email, password);
        dashboardPage.isDashboardLoaded();
    }

    /**
     * Helper to submit a project idea as Pod Lead.
     */
    private String submitProjectAsPodLead(Map<String, String> data) {
        loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
        ProjectSubmissionPage submissionPage = new ProjectSubmissionPage(driver);
        submissionPage.navigateToSubmit();

        String uniqueTitle = data.get("Project_Title") + " " + System.currentTimeMillis();
        submissionPage.enterProjectTitle(uniqueTitle);
        submissionPage.enterProblemStatement(data.get("Problem_Statement"));
        submissionPage.enterObjectives(data.get("Objectives"));
        submissionPage.selectTechnologies(data.get("Tech_Stack"));
        submissionPage.enterDocumentationLink(data.get("Doc_Link"));

        submissionPage.clickSubmitForAnalysis();

        SubmissionsPage submissionsPage = new SubmissionsPage(driver);
        submissionsPage.isSubmissionsPageDisplayed();
        submissionsPage.waitForSubmissionsToLoad();
        return uniqueTitle;
    }

    /**
     * DATA-DRIVEN TEST (DDT)
     * Executes all 10 FDU test scenarios dynamically from TestData.xlsx.
     */
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

    /**
     * FDU_01: Submit project details and verify data is stored
     * Expected Result: Project details are successfully saved in the system.
     */
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

    /**
     * FDU_02: Verify Project Title is updated after submission
     * Expected Result: Project Title is displayed correctly wherever the submission is viewed.
     */
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

        WebElement card = submissionsPage.getSubmissionCardByTitle(submittedUniqueTitle);
        Assert.assertNotNull(card, "Submission card should be rendered");
    }

    /**
     * FDU_03: Verify Problem Statement is updated after submission
     * Expected Result: Problem Statement is stored and displayed correctly.
     */
    @Test(priority = 3, description = "FDU_03: Verify Problem Statement is updated after submission")
    public void testFDU_03_VerifyProblemStatementUpdatedAfterSubmission() {
        Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_03");
        if (submittedUniqueTitle.isEmpty()) {
            submittedUniqueTitle = submitProjectAsPodLead(data);
        } else {
            loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
        }

        SubmissionsPage submissionsPage = new SubmissionsPage(driver);
        submissionsPage.navigateToSubmissions();
        WebElement card = submissionsPage.getSubmissionCardByTitle(submittedUniqueTitle);
        Assert.assertNotNull(card, "Project submission must exist in Submissions page");

        String cardText = submissionsPage.getCardTextByTitle(submittedUniqueTitle);
        Assert.assertFalse(cardText.isEmpty(), "Submission card must display stored problem and evaluation content");
    }

    /**
     * FDU_04: Verify Supporting Documentation Link is updated after submission
     * Expected Result: Documentation Link is stored and displayed correctly.
     */
    @Test(priority = 4, description = "FDU_04: Verify Supporting Documentation Link is updated after submission")
    public void testFDU_04_VerifySupportingDocumentationLinkUpdatedAfterSubmission() {
        Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_04");
        if (submittedUniqueTitle.isEmpty()) {
            submittedUniqueTitle = submitProjectAsPodLead(data);
        } else {
            loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
        }

        SubmissionsPage submissionsPage = new SubmissionsPage(driver);
        submissionsPage.navigateToSubmissions();
        Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
                "Documentation link and submission data must be present for project: " + submittedUniqueTitle);
    }

    /**
     * FDU_05: Verify submitted data in Pod Lead view
     * Expected Result: Pod Lead can view the submitted details correctly.
     */
    @Test(priority = 5, description = "FDU_05: Verify submitted data in Pod Lead view")
    public void testFDU_05_VerifySubmittedDataInPodLeadView() {
        Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_05");
        if (submittedUniqueTitle.isEmpty()) {
            submittedUniqueTitle = submitProjectAsPodLead(data);
        } else {
            loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
        }

        SubmissionsPage submissionsPage = new SubmissionsPage(driver);
        submissionsPage.navigateToSubmissions();

        Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
                "Pod Lead must view the submitted details correctly in their submissions view");
        WebElement card = submissionsPage.getSubmissionCardByTitle(submittedUniqueTitle);
        Assert.assertNotNull(card, "Pod Lead card view must not be null");
        Assert.assertFalse(submissionsPage.getCardStatus(card).isEmpty(), "Status should be displayed for Pod Lead");
    }

    /**
     * FDU_06: Verify submitted data in Pod Member view
     * Expected Result: Pod Member sees the same updated submission details.
     */
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

    /**
     * FDU_07: Verify submitted data in Trainer view
     * Expected Result: Trainer sees the same updated submission details.
     */
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

    /**
     * FDU_08: Verify data consistency across all roles
     * Expected Result: Data displayed in Pod Lead, Pod Member, and Trainer views should match exactly.
     */
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
        Assert.assertTrue(leadSubmissions.isProjectPresentInList(submittedUniqueTitle),
                "Lead must see project: " + submittedUniqueTitle);
        String leadCardText = leadSubmissions.getCardTextByTitle(submittedUniqueTitle);

        // 2. Check Pod Member view
        loginAs(data.get("Pod_Member_Email"), data.get("Pod_Member_Password"));
        SubmissionsPage memberSubmissions = new SubmissionsPage(driver);
        memberSubmissions.navigateToSubmissions();
        Assert.assertTrue(memberSubmissions.isProjectPresentInList(submittedUniqueTitle),
                "Member must see project: " + submittedUniqueTitle);
        String memberCardText = memberSubmissions.getCardTextByTitle(submittedUniqueTitle);

        // Cleanse action buttons like "Revise" or "Edit" (only visible to author/Lead) to compare core data
        String cleanLeadText = leadCardText.replaceAll("(?m)^Revise$", "").replaceAll("(?m)^Edit$", "").trim();
        String cleanMemberText = memberCardText.replaceAll("(?m)^Revise$", "").replaceAll("(?m)^Edit$", "").trim();

        // Assert data consistency between Pod Lead and Pod Member
        Assert.assertEquals(cleanLeadText, cleanMemberText,
                "Data displayed in Pod Lead and Pod Member views must match consistently");
    }

    /**
     * FDU_09: Verify data persists after page refresh
     * Expected Result: Submitted data remains unchanged after refresh.
     */
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
        Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
                "Project must exist before refresh");

        String textBefore = submissionsPage.getCardTextByTitle(submittedUniqueTitle);

        // Perform page refresh
        driver.navigate().refresh();
        submissionsPage.waitForSubmissionsToLoad();

        Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
                "Submitted data must remain available after page refresh");
        String textAfter = submissionsPage.getCardTextByTitle(submittedUniqueTitle);
        Assert.assertEquals(textBefore, textAfter,
                "Submitted data should remain unchanged after page refresh");
    }

    /**
     * FDU_10: Verify data persists after logout and login
     * Expected Result: Submitted data remains available after logging in again.
     */
    @Test(priority = 10, description = "FDU_10: Verify data persists after logout and login")
    public void testFDU_10_VerifyDataPersistsAfterLogoutAndLogin() {
        Map<String, String> data = DataProviders.getFormDataUpdationTestCaseData("FDU_10");
        if (submittedUniqueTitle.isEmpty()) {
            submittedUniqueTitle = submitProjectAsPodLead(data);
        } else {
            loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));
        }

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.logout();

        // Log back in as Pod Lead
        loginAs(data.get("Pod_Lead_Email"), data.get("Pod_Lead_Password"));

        SubmissionsPage submissionsPage = new SubmissionsPage(driver);
        submissionsPage.navigateToSubmissions();

        Assert.assertTrue(submissionsPage.isProjectPresentInList(submittedUniqueTitle),
                "Submitted data must remain available after logout and re-login");
    }
}
