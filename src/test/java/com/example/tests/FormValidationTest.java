package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;
import com.example.pages.ProjectSubmissionPage;
import com.example.pages.SubmissionsPage;
import com.example.utils.DataProviders;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class FormValidationTest extends BaseTest {

    private ProjectSubmissionPage submissionPage;
    private SubmissionsPage submissionsPage;

    @BeforeMethod
    public void loginAsPodLead() {
        // Fetch Pod Lead credentials from Excel (FormValidationWorkflow sheet)
        Map<String, String> data = DataProviders.getFormValidationTestCaseData("PL-04");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(data.get("User_Email"), data.get("User_Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.isDashboardLoaded();

        submissionPage = new ProjectSubmissionPage(driver);
        submissionsPage = new SubmissionsPage(driver);
    }

    /**
     * DATA-DRIVEN TEST (DDT)
     * Reads all Form Validation test scenarios directly from TestData.xlsx.
     */
    @Test(dataProvider = "formValidationDDT", dataProviderClass = DataProviders.class, priority = 0,
          description = "Data-Driven Test: Verify Form Validation test scenarios from Excel")
    public void testDataDrivenFormValidationFromExcel(Map<String, String> testData) {
        String tcId = testData.get("TC_ID");
        String expectedResult = testData.get("Expected_Result");

        Assert.assertNotNull(tcId, "TC_ID must not be null in Excel");
        Assert.assertNotNull(expectedResult, "Expected_Result must not be null in Excel");

        if ("PL-04".equalsIgnoreCase(tcId)) {
            submissionPage.navigateToSubmit();
            Assert.assertTrue(driver.getCurrentUrl().contains(testData.get("Expected_Status")),
                    "URL should contain " + testData.get("Expected_Status"));
            Assert.assertTrue(submissionPage.isSubmissionFormDisplayed(),
                    "Form should be displayed for Pod Lead");
        } else if ("PL-05".equalsIgnoreCase(tcId)) {
            submissionPage.navigateToSubmit();
            submissionPage.clickSubmitForAnalysis();
            Assert.assertTrue(submissionPage.isSubmissionBlocked(),
                    "Submission should be blocked when mandatory fields are missing");
        } else if ("PL-06".equalsIgnoreCase(tcId)) {
            submissionPage.navigateToSubmit();
            Assert.assertTrue(submissionPage.isSubmissionFormDisplayed(),
                    "Submission form should be displayed");
        } else if ("PL-07".equalsIgnoreCase(tcId)) {
            submissionsPage.navigateToSubmissions();
            Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
                    "Submissions page should be displayed");
        }
    }

    /**
     * PL-04: Open the new project submission page.
     * Preconditions: Pod Lead is logged in.
     * Test Steps: Navigate to 'Submit idea' from dashboard sidebar.
     * Expected: The form opens for the Pod Lead and displays the active cohort criteria
     *           and the 70% minimum alignment requirement.
     */
    @Test(priority = 1, description = "PL-04: Open the new project submission page")
    public void testPL04_OpenNewProjectSubmissionPage() {
        Map<String, String> data = DataProviders.getFormValidationTestCaseData("PL-04");

        submissionPage.navigateToSubmit();

        // 1. Verify form opens for the Pod Lead
        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_Status")),
                "URL should contain: " + data.get("Expected_Status"));
        Assert.assertTrue(submissionPage.isSubmissionFormDisplayed(),
                "Project submission form should be opened and visible for Pod Lead");

        // 2. Verify active cohort criteria is displayed
        Assert.assertTrue(submissionPage.isCohortCriteriaDisplayed(),
                "Active cohort criteria should be displayed on submission page");
        String cohortText = submissionPage.getCohortDetailsText();
        Assert.assertTrue(cohortText.contains(data.get("Expected_Cohort_Title")),
                "Cohort title should match: " + data.get("Expected_Cohort_Title"));

        // 3. Verify the 70% minimum alignment requirement is displayed
        Assert.assertTrue(submissionPage.isMinimumAlignmentRequirementDisplayed(),
                "70% minimum alignment requirement should be visible to Pod Lead");
        Assert.assertTrue(cohortText.contains(data.get("Expected_Min_Alignment")),
                "Minimum alignment requirement text should contain: " + data.get("Expected_Min_Alignment"));
    }

    /**
     * PL-05: Submit the form with required information missing.
     * Preconditions: Pod Lead is on the project submission page.
     * Test Steps: Attempt submission with required fields left empty.
     * Expected: Project title, problem statement, objectives and technology stack are validated.
     *           Submission is blocked until required fields are completed; documentation link remains optional.
     */
    @Test(priority = 2, description = "PL-05: Submit the form with required information missing")
    public void testPL05_SubmitFormWithRequiredInformationMissing() {
        Map<String, String> data = DataProviders.getFormValidationTestCaseData("PL-05");

        submissionPage.navigateToSubmit();

        // Click submit without entering any information
        submissionPage.clickSubmitForAnalysis();

        // 1. Verify validation alert / error messages
        Assert.assertTrue(submissionPage.isValidationAlertDisplayed(),
                "Validation alert should be displayed when submitting empty form");
        String alertText = submissionPage.getValidationAlertText();
        Assert.assertTrue(alertText.contains(data.get("Expected_Status")),
                "Alert should contain: " + data.get("Expected_Status"));

        // 2. Verify mandatory fields (title, problem statement, objectives, tech stack) are validated
        List<String> fieldErrors = submissionPage.getFieldErrors();
        Assert.assertFalse(fieldErrors.isEmpty(), "Field-level validation error messages should appear");

        boolean titleValidated = fieldErrors.stream().anyMatch(e -> e.toLowerCase().contains("title"));
        boolean problemValidated = fieldErrors.stream().anyMatch(e -> e.toLowerCase().contains("problem"));
        boolean objectivesValidated = fieldErrors.stream().anyMatch(e -> e.toLowerCase().contains("objectives"));
        boolean techStackValidated = fieldErrors.stream().anyMatch(e -> e.toLowerCase().contains("technology"));

        Assert.assertTrue(titleValidated, "Project title must be validated");
        Assert.assertTrue(problemValidated, "Problem statement must be validated");
        Assert.assertTrue(objectivesValidated, "Objectives must be validated");
        Assert.assertTrue(techStackValidated, "Technology stack must be validated");

        // 3. Verify documentation link remains optional (no error message for doc link)
        boolean docLinkHasError = fieldErrors.stream().anyMatch(e -> e.toLowerCase().contains("documentation"));
        Assert.assertFalse(docLinkHasError, "Documentation link should remain optional and not throw error");

        // 4. Verify submission is blocked (user remains on /submit)
        Assert.assertTrue(submissionPage.isSubmissionBlocked(),
                "Submission should be blocked and remain on /submit page");
    }

    /**
     * PL-06: Submit a complete project idea.
     * Preconditions: Pod Lead is on the project submission page.
     * Test Steps: Fill all required fields (title, problem statement, objectives, tech stack)
     *             and optional documentation link, then submit.
     * Expected: The submission is saved, evaluation is initiated and the project appears
     *           in the Pod Lead's submissions list with its current status.
     */
    @Test(priority = 3, description = "PL-06: Submit a complete project idea")
    public void testPL06_SubmitCompleteProjectIdea() {
        Map<String, String> data = DataProviders.getFormValidationTestCaseData("PL-06");

        submissionPage.navigateToSubmit();

        // Append timestamp to ensure uniquely identifiable submission
        String uniqueTitle = data.get("Project_Title") + " " + System.currentTimeMillis();

        submissionPage.enterProjectTitle(uniqueTitle);
        submissionPage.enterProblemStatement(data.get("Problem_Statement"));
        submissionPage.enterObjectives(data.get("Objectives"));
        submissionPage.selectTechnologies(data.get("Tech_Stack"));
        submissionPage.enterDocumentationLink(data.get("Doc_Link"));

        submissionPage.clickSubmitForAnalysis();

        // 1. Verify navigation to submissions page (saved & evaluation initiated)
        Assert.assertTrue(submissionsPage.isSubmissionsPageDisplayed(),
                "After submission, user should be redirected to Submissions list");

        // 2. Verify project appears in Pod Lead's submissions list with its current status
        submissionsPage.waitForSubmissionsToLoad();
        Assert.assertTrue(submissionsPage.isProjectPresentInList(uniqueTitle),
                "Newly submitted project idea should appear in the Pod Lead's submissions list");

        WebElement newCard = submissionsPage.getSubmissionCardByTitle(uniqueTitle);
        Assert.assertNotNull(newCard, "Project card must be found in submissions list");
        String status = submissionsPage.getCardStatus(newCard);
        Assert.assertFalse(status.isEmpty(), "Project must have a current evaluation status displayed");
    }

    /**
     * PL-07: Verify a project evaluated below 70% alignment.
     * Preconditions: Pod Lead has submissions with alignment evaluation below 70%.
     * Test Steps: Inspect project card evaluated below 70% alignment threshold.
     * Expected: The project is marked 'Needs Revision' (NEEDS_REVISION) and the result
     *           clearly shows the score, analysis summary, matched criteria and missing criteria.
     */
    @Test(priority = 4, description = "PL-07: Verify a project evaluated below 70% alignment")
    public void testPL07_VerifyProjectEvaluatedBelow70Alignment() {
        Map<String, String> data = DataProviders.getFormValidationTestCaseData("PL-07");

        submissionsPage.navigateToSubmissions();

        // Find a project in Pod Lead submissions evaluated below 70% alignment
        WebElement belowThresholdProject = submissionsPage.findProjectEvaluatedBelowThreshold(70);
        Assert.assertNotNull(belowThresholdProject,
                "Expected at least one project evaluated below 70% alignment in submissions list");

        // 1. Verify project is marked Needs Revision (NEEDS_REVISION)
        String status = submissionsPage.getCardStatus(belowThresholdProject);
        Assert.assertTrue(status.contains(data.get("Expected_Status")),
                "Project evaluated below 70% alignment must be marked " + data.get("Expected_Status") + " but got: " + status);

        // 2. Verify alignment score is displayed and below 70%
        String scoreText = submissionsPage.getCardScore(belowThresholdProject);
        Assert.assertFalse(scoreText.isEmpty(), "Alignment score must be displayed");
        int score = Integer.parseInt(scoreText.replace("%", "").trim());
        Assert.assertTrue(score < 70, "Score should be strictly below 70% but was: " + score);

        // 3. Verify analysis summary is clearly shown
        String analysis = submissionsPage.getCardAnalysis(belowThresholdProject);
        Assert.assertFalse(analysis.isEmpty(), "Analysis summary should be clearly shown");

        // 4. Verify matched criteria is clearly shown
        String matchedCriteria = submissionsPage.getCardMatchedCriteria(belowThresholdProject);
        Assert.assertFalse(matchedCriteria.isEmpty(), "Matched criteria should be clearly shown");

        // 5. Verify missing criteria is clearly shown
        String missingCriteria = submissionsPage.getCardMissingCriteria(belowThresholdProject);
        Assert.assertFalse(missingCriteria.isEmpty(), "Missing criteria should be clearly shown");
    }
}
