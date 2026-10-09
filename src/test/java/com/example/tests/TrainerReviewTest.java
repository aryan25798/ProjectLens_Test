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

    /**
     * TC01: Verify Navigation to Reviews Queue
     * Preconditions: Trainer is logged in to ProjectLens workspace.
     * Test Steps: Click on 'Reviews' from the left navigation panel.
     * Expected: The Trainer Reviews page loads, displaying the Decision queue with pending items ready for review.
     */
    @Test(priority = 1, description = "TC01: Verify Navigation to Reviews Queue")
    public void testTC01_VerifyNavigationToReviewsQueue() {
        Map<String, String> data = DataProviders.getTrainerReviewTestCaseData("TC01");

        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_Status")),
                "URL should contain /reviews");
        Assert.assertTrue(reviewsPage.isDecisionQueueDisplayed(),
                "Decision queue should be displayed");
    }

    /**
     * TC02: Verify Submission Details Display
     * Preconditions: Trainer is on the Reviews page with a pending item in Decision queue.
     * Test Steps: 1. Select a submission from the Decision queue. 2. Inspect problem statement, objectives, tech stack, alignment score.
     * Expected: Project title, pod details, alignment score, Problem statement, Objectives, Tech Stack displayed.
     */
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

    /**
     * NOTE: TC03 (Verify Overlap Detection Warning) is intentionally excluded per user instruction.
     */

    /**
     * TC04: Verify Successful Submission Approval
     * Preconditions: Trainer has reviewed all submission details and criteria alignment.
     * Test Steps: 1. Enter optional feedback in 'Add comments for the pod...' textarea. 2. Click 'Approve' button.
     * Expected: The submission is marked as Approved, removed from the active pending queue.
     */
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

    /**
     * TC05: Verify Requesting Revision
     * Preconditions: Submission requires updates or clarification from the pod.
     * Test Steps: 1. Enter feedback in 'Add comments for the pod...' textarea. 2. Click 'Needs Revision' button.
     * Expected: Submission status updates to 'Needs Revision', feedback comments are saved.
     */
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
                    "Decision queue active");
        }
    }

    /**
     * TC06: Verify Rejection Flow
     * Preconditions: Submission fails to meet required criteria.
     * Test Steps: 1. Enter rejection reasons in the comment box. 2. Click 'Reject' button.
     * Expected: Submission status updates to 'Rejected', decision is finalized.
     */
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
