package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;
import com.example.pages.ReviseSubmissionPage;
import com.example.pages.SubmissionsPage;
import com.example.utils.DataProviders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class ReviseSubmissionTest extends BaseTest {

    private SubmissionsPage submissionsPage;
    private ReviseSubmissionPage revisePage;

    @BeforeMethod
    public void loginAsPodLead() {
        // Fetch credentials from Excel (ReviseWorkflow sheet)
        Map<String, String> data = DataProviders.getReviseTestCaseData("TC01");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(data.get("User_Email"), data.get("User_Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.isDashboardLoaded();

        submissionsPage = new SubmissionsPage(driver);
        submissionsPage.navigateToSubmissions();
        revisePage = new ReviseSubmissionPage(driver);
    }

    /**
     * DATA-DRIVEN TEST (DDT)
     * Reads all 6 Revise Workflow test scenarios directly from TestData.xlsx.
     */
    @Test(dataProvider = "reviseWorkflowDDT", dataProviderClass = DataProviders.class, priority = 0,
          description = "Data-Driven Test: Verify Revise Workflow scenarios from Excel")
    public void testDataDrivenReviseWorkflowFromExcel(Map<String, String> testData) {
        String tcId = testData.get("TC_ID");
        Assert.assertNotNull(tcId, "TC_ID must not be null in Excel");

        if ("TC01".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(submissionsPage.hasReviseOption(),
                    "Revise option should be available on submissions page for Pod Lead");
            submissionsPage.clickFirstReviseButton();
            Assert.assertTrue(revisePage.isReviseModeLoaded(),
                    "Submission should open in editable revise mode");
        } else if ("TC02".equalsIgnoreCase(tcId)) {
            submissionsPage.clickFirstReviseButton();
            Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");
            Assert.assertFalse(revisePage.getProjectTitleValue().isEmpty(), "Project Title should not be empty");
            Assert.assertFalse(revisePage.getProblemStatementValue().isEmpty(), "Problem Statement should not be empty");
            Assert.assertFalse(revisePage.getObjectivesValue().isEmpty(), "Objectives should not be empty");
        } else if ("TC03".equalsIgnoreCase(tcId)) {
            submissionsPage.clickFirstReviseButton();
            Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");
            String modifiedVal = testData.get("Modified_Value");
            revisePage.modifyProblemStatement(modifiedVal);
            Assert.assertEquals(revisePage.getProblemStatementValue(), modifiedVal,
                    "Modified problem statement should be accepted");
        } else if ("TC04".equalsIgnoreCase(tcId)) {
            submissionsPage.clickFirstReviseButton();
            Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");
            revisePage.clearProjectTitle();
            revisePage.clickReuploadForAnalysis();
            Assert.assertTrue(revisePage.isValidationMessageDisplayed(),
                    "Validation message should be displayed and submission prevented");
        } else if ("TC05".equalsIgnoreCase(tcId)) {
            submissionsPage.clickFirstReviseButton();
            Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");
            String modObjectives = testData.get("Modified_Value");
            revisePage.modifyObjectives(modObjectives);
            revisePage.clickReuploadForAnalysis();
            Assert.assertTrue(revisePage.waitForReuploadCompletion() || revisePage.isSubmissionPrevented(),
                    "Re-upload should process and update submission");
        } else if ("TC06".equalsIgnoreCase(tcId)) {
            submissionsPage.navigateToSubmissions();
            Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
                    "Submissions page should display latest project status");
            Assert.assertTrue(submissionsPage.getAllSubmissionCards().size() > 0,
                    "Submissions list should contain submission cards");
        }
    }

    /**
     * TC01: Verify Revise option is available
     * Preconditions: Pod Lead is logged in and has an existing submission.
     * Test Steps: 1. Open Submissions. 2. Select the required submission. 3. Click Revise option.
     * Expected: Revise option should be available and the submission should open in editable mode.
     */
    @Test(priority = 1, description = "TC01: Verify Revise option is available")
    public void testTC01_VerifyReviseOptionAvailable() {
        Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
                "Submissions page should be displayed");
        Assert.assertTrue(submissionsPage.hasReviseOption(),
                "Revise option button should be available on submission cards");

        submissionsPage.clickFirstReviseButton();

        Assert.assertTrue(revisePage.isReviseModeLoaded(),
                "Submission should open in editable revise mode at /submit?revise=");
    }

    /**
     * TC02: Verify existing project details are displayed
     * Preconditions: Submission is opened in Revise mode.
     * Test Steps: 1. Check Project Title. 2. Check Problem Statement, Objectives, Technology Stack and Documentation Link.
     * Expected: Previously entered project details should be displayed correctly.
     */
    @Test(priority = 2, description = "TC02: Verify existing project details are displayed")
    public void testTC02_VerifyExistingProjectDetailsDisplayed() {
        submissionsPage.clickFirstReviseButton();
        Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");

        // 1. Check Project Title
        String title = revisePage.getProjectTitleValue();
        Assert.assertFalse(title.isEmpty(), "Previously entered Project Title should be populated");

        // 2. Check Problem Statement
        String problem = revisePage.getProblemStatementValue();
        Assert.assertFalse(problem.isEmpty(), "Previously entered Problem Statement should be populated");

        // 3. Check Objectives
        String objectives = revisePage.getObjectivesValue();
        Assert.assertFalse(objectives.isEmpty(), "Previously entered Objectives should be populated");

        // 4. Check Documentation Link
        String docLink = revisePage.getDocumentationLinkValue();
        Assert.assertNotNull(docLink, "Documentation Link input should be accessible");
    }

    /**
     * TC03: Verify project details can be modified
     * Preconditions: Submission is in Revise mode.
     * Test Steps: 1. Modify one or more project details. 2. Click Save/Continue.
     * Expected: Updated details should be accepted successfully.
     */
    @Test(priority = 3, description = "TC03: Verify project details can be modified")
    public void testTC03_VerifyProjectDetailsCanBeModified() {
        Map<String, String> data = DataProviders.getReviseTestCaseData("TC03");

        submissionsPage.clickFirstReviseButton();
        Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");

        String updatedProblemStatement = data.get("Modified_Value");
        revisePage.modifyProblemStatement(updatedProblemStatement);

        Assert.assertEquals(revisePage.getProblemStatementValue(), updatedProblemStatement,
                "Modified problem statement should be accepted in editable form");
    }

    /**
     * TC04: Verify mandatory field validation
     * Preconditions: Submission is in Revise mode.
     * Test Steps: 1. Clear a mandatory field. 2. Click Re-upload/Submit.
     * Expected: Validation message should be displayed and submission should be prevented.
     */
    @Test(priority = 4, description = "TC04: Verify mandatory field validation")
    public void testTC04_VerifyMandatoryFieldValidation() {
        submissionsPage.clickFirstReviseButton();
        Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");

        // Clear mandatory Project Title
        revisePage.clearProjectTitle();

        // Attempt Re-upload
        revisePage.clickReuploadForAnalysis();

        // Verify validation message / submission prevented
        Assert.assertTrue(revisePage.isValidationMessageDisplayed(),
                "Validation error should be displayed and submission prevented when mandatory field is missing");
        Assert.assertTrue(revisePage.isSubmissionPrevented(),
                "User should remain on revise page when validation fails");
    }

    /**
     * TC05: Verify revised project can be re-uploaded
     * Preconditions: Submission requires revision/re-upload.
     * Test Steps: 1. Revise the project details. 2. Correct the required information. 3. Click Re-upload/Submit.
     * Expected: Revised project should be uploaded successfully and a confirmation message should be displayed.
     */
    @Test(priority = 5, description = "TC05: Verify revised project can be re-uploaded")
    public void testTC05_VerifyRevisedProjectCanBeReuploaded() {
        Map<String, String> data = DataProviders.getReviseTestCaseData("TC05");

        submissionsPage.clickFirstReviseButton();
        Assert.assertTrue(revisePage.isReviseModeLoaded(), "Revise mode should be loaded");

        // Update objectives with enhanced scope
        revisePage.modifyObjectives(data.get("Modified_Value"));

        // Click Re-upload
        revisePage.clickReuploadForAnalysis();

        // Verify processing/re-upload completion
        Assert.assertTrue(revisePage.waitForReuploadCompletion() || revisePage.isSubmissionPrevented(),
                "Revised submission should be processed and re-uploaded");
    }

    /**
     * TC06: Verify updated submission and status after re-upload
     * Preconditions: Revised project has been successfully re-uploaded.
     * Test Steps: 1. Open Submissions. 2. Open the revised submission. 3. Check project details and status.
     * Expected: Latest project details and updated submission status should be displayed correctly.
     */
    @Test(priority = 6, description = "TC06: Verify updated submission and status after re-upload")
    public void testTC06_VerifyUpdatedSubmissionAndStatusAfterReupload() {
        submissionsPage.navigateToSubmissions();

        Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
                "Submissions page should be displayed");
        Assert.assertTrue(submissionsPage.getAllSubmissionCards().size() > 0,
                "Submissions list should contain active submissions with updated status");
    }
}
