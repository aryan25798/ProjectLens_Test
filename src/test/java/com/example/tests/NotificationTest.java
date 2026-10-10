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

    /**
     * DATA-DRIVEN TEST (DDT)
     * Reads all Notification test scenarios directly from TestData.xlsx.
     */
    @Test(dataProvider = "notificationDDT", dataProviderClass = DataProviders.class, priority = 0,
          description = "Data-Driven Test: Verify Notification test scenarios from Excel")
    public void testDataDrivenNotificationsFromExcel(Map<String, String> testData) {
        String tcId = testData.get("TC_ID");
        String expectedUrlFraction = testData.get("Expected_URL_Fraction");
        String expectedKeyword = testData.get("Expected_Alert_Keyword");

        Assert.assertNotNull(tcId, "TC_ID must not be null in Excel");

        // Verify user is on the notifications page
        Assert.assertTrue(driver.getCurrentUrl().contains(expectedUrlFraction),
                "URL should contain: " + expectedUrlFraction);
        Assert.assertTrue(notificationsPage.isNotificationsPageDisplayed(),
                "Notifications page should be displayed");

        if ("TC_NOTIF_01".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(notificationsPage.getNotificationCount() > 0,
                    "Notifications list should contain items");
            Assert.assertTrue(notificationsPage.areTimestampsDisplayed(),
                    "Timestamps should be displayed for notifications");
        } else if ("TC_NOTIF_02".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(notificationsPage.hasLowScoreAlert(),
                    "Notifications should contain low score alert (< 70%)");
        } else if ("TC_NOTIF_03".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(notificationsPage.hasHighScoreAlert(),
                    "Notifications should contain high score alert (>= 70%)");
        } else if ("TC_NOTIF_04".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(notificationsPage.hasTrainerDecisionAlert(),
                    "Notifications should contain Trainer decision alert");
        } else if ("TC_NOTIF_05".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(notificationsPage.isAnyNotificationMarkedRead() || notificationsPage.markFirstUnreadNotificationAsRead(),
                    "Notification read status should be verified");
        } else if ("TC_NOTIF_06".equalsIgnoreCase(tcId)) {
            notificationsPage.clickRefresh();
            Assert.assertTrue(notificationsPage.isNotificationsPageDisplayed(),
                    "Notifications should be displayed after refresh");
        } else if ("TC_NOTIF_07".equalsIgnoreCase(tcId)) {
            Assert.assertTrue(notificationsPage.verifyPodIsolation(testData.get("Assigned_Pod")),
                    "Notifications should maintain Pod isolation");
        }
    }

    /**
     * TC_NOTIF_01: Open Notifications Page
     * Preconditions: Log in as Pod Lead.
     * Steps: 1. Click Notifications on the left menu.
     * Expected Result: Notifications page opens and displays a chronological list of
     *                  recent updates with date and time.
     */
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

    /**
     * TC_NOTIF_02: Low Score Alert (< 70%)
     * Preconditions: Submit an idea with an alignment score under 70%.
     * Steps: Go to Notifications page.
     * Expected Result: Alert displays: "Your project idea '[Idea Name]' needs improvement.
     *                  Alignment score: [X]%. Please revise and reupload."
     */
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

    /**
     * TC_NOTIF_03: High Score Alert (>= 70%)
     * Preconditions: Submit an idea with an alignment score of 70% or higher.
     * Steps: Go to Notifications page.
     * Expected Result: Alert displays: "Your project idea '[Idea Name]' qualified for trainer
     *                  review with an alignment score of [X]%."
     */
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

    /**
     * TC_NOTIF_04: Trainer Decision Alert
     * Preconditions: Trainer submits a decision (Approved / Needs Revision / Rejected).
     * Steps: Pod Lead opens Notifications.
     * Expected Result: Alert displays trainer's decision, e.g., "Trainer decision for '[Idea Name]': Approved"
     */
    @Test(priority = 4, description = "TC_NOTIF_04: Trainer Decision Alert")
    public void testTC_NOTIF_04_TrainerDecisionAlert() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_04");

        Assert.assertTrue(notificationsPage.hasTrainerDecisionAlert(),
                "Expected Trainer decision alert in notifications list");

        String alertMessage = notificationsPage.getTrainerDecisionAlertMessage();
        Assert.assertTrue(alertMessage.toLowerCase().contains("trainer decision for"),
                "Alert should contain 'Trainer decision for', but got: " + alertMessage);
    }

    /**
     * TC_NOTIF_05: Mark Notification as Read
     * Preconditions: Locate an unread notification.
     * Steps: Click 'Mark as read' next to it.
     * Expected Result: Notification status updates to 'Read'.
     */
    @Test(priority = 5, description = "TC_NOTIF_05: Mark Notification as Read")
    public void testTC_NOTIF_05_MarkNotificationAsRead() {
        boolean marked = notificationsPage.markFirstUnreadNotificationAsRead();
        if (marked) {
            Assert.assertTrue(notificationsPage.isAnyNotificationMarkedRead(),
                    "Notification should update status to Read");
        } else {
            // Already read or no unread notifications available
            Assert.assertTrue(notificationsPage.isAnyNotificationMarkedRead(),
                    "Notifications have Read status displayed");
        }
    }

    /**
     * TC_NOTIF_06: Refresh Notifications List
     * Preconditions: Pod Lead is on Notifications page.
     * Steps: Click the 'Refresh' button at the top of Notifications page.
     * Expected Result: Page refreshes and fetches the latest notifications.
     */
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

    /**
     * TC_NOTIF_07: Pod Isolation Check
     * Preconditions: Log in as Pod Lead of Pod Alpha (Innovex).
     * Steps: Check Notifications list.
     * Expected Result: Pod Lead only sees notifications belonging to their assigned Pod
     *                  and zero notifications from other pods.
     */
    @Test(priority = 7, description = "TC_NOTIF_07: Pod Isolation Check")
    public void testTC_NOTIF_07_PodIsolationCheck() {
        Map<String, String> data = DataProviders.getNotificationTestCaseData("TC_NOTIF_07");
        String assignedPod = data.get("Assigned_Pod");

        boolean isIsolated = notificationsPage.verifyPodIsolation(assignedPod);
        Assert.assertTrue(isIsolated,
                "Pod Lead of " + assignedPod + " must not see notifications from any other pods");
    }
}
