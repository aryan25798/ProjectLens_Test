package com.example.tests;

import com.example.base.BaseTest;
import com.example.pages.DashboardPage;
import com.example.pages.LoginPage;
import com.example.utils.DataProviders;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class LoginTest extends BaseTest {

    /**
     * INDUSTRY GRADE DATA-DRIVEN TEST (DDT)
     * Reads all VALID and INVALID login combinations directly from TestData.xlsx.
     * Iterates over every row dynamically without hardcoding.
     */
    @Test(dataProvider = "loginCredentialsDDT", dataProviderClass = DataProviders.class, priority = 0,
          description = "Data-Driven Test: Verify Valid & Invalid login scenarios from Excel")
    public void testDataDrivenLoginFromExcel(Map<String, String> testData) {
        String testType = testData.get("Test_Type");
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedResult = testData.get("Expected_Result");
        String expectedErrorMessage = testData.get("Expected_Error_Message");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);

        if ("VALID".equalsIgnoreCase(testType)) {
            DashboardPage dashboardPage = new DashboardPage(driver);
            Assert.assertTrue(dashboardPage.isDashboardLoaded(),
                    "Expected successful navigation to dashboard for valid user: " + username);
        } else if ("INVALID".equalsIgnoreCase(testType)) {
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                    "Expected error alert displayed for invalid user: " + username);
            if (expectedErrorMessage != null && !expectedErrorMessage.isEmpty()) {
                Assert.assertTrue(loginPage.getErrorMessage().contains(expectedErrorMessage),
                        "Expected error alert to contain: " + expectedErrorMessage);
            }
            Assert.assertTrue(driver.getCurrentUrl().contains("/login"),
                    "User should remain on login page for invalid credentials");
        }
    }

    /**
     * TC01: Verify Login Page Display
     */
    @Test(priority = 1, description = "TC01: Verify Login Page Display")
    public void testTC01_VerifyLoginPageDisplay() {
        Map<String, String> data = DataProviders.getTestCaseData("TC01");
        LoginPage loginPage = new LoginPage(driver);

        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login inputs and button should be visible");
        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")), "URL should contain /login");
    }

    /**
     * TC02: Verify Login with Valid Credentials
     */
    @Test(priority = 2, description = "TC02: Verify Login with Valid Credentials")
    public void testTC02_VerifyLoginWithValidCredentials() {
        Map<String, String> data = DataProviders.getTestCaseData("TC02");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard should load after valid credentials");
    }

    /**
     * TC03: Verify Login with Invalid Credentials
     */
    @Test(priority = 3, description = "TC03: Verify Login with Invalid Credentials")
    public void testTC03_VerifyLoginWithInvalidCredentials() {
        Map<String, String> data = DataProviders.getTestCaseData("TC03");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error alert should be displayed for invalid credentials");
        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")), "User should remain on login page");
    }

    /**
     * TC04: Verify Mandatory Field Validation
     */
    @Test(priority = 4, description = "TC04: Verify Mandatory Field Validation")
    public void testTC04_VerifyMandatoryFieldValidation() {
        Map<String, String> data = DataProviders.getTestCaseData("TC04");
        LoginPage loginPage = new LoginPage(driver);

        boolean isSignInEnabled = loginPage.isSignInButtonEnabled();
        Assert.assertFalse(isSignInEnabled, "Sign In button must be disabled when fields are blank (Angular reactive form validation)");
        Assert.assertTrue(driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")), "User should remain on login page");
    }

    /**
     * TC05: Verify Password Masking
     */
    @Test(priority = 5, description = "TC05: Verify Password Masking")
    public void testTC05_VerifyPasswordMasking() {
        Map<String, String> data = DataProviders.getTestCaseData("TC05");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.enterPassword(data.get("Password"));
        String inputType = loginPage.getPasswordInputType();
        Assert.assertEquals(inputType, "password", "Password field input type must be 'password' (masked)");

        loginPage.toggleShowPassword();
        Assert.assertEquals(loginPage.getPasswordInputType(), "text", "Password input type should become 'text' when unmasked");
    }

    /**
     * TC06: Verify Trainer Role Dashboard
     */
    @Test(priority = 6, description = "TC06: Verify Trainer Role Dashboard")
    public void testTC06_VerifyTrainerRoleDashboard() {
        Map<String, String> data = DataProviders.getTestCaseData("TC06");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Trainer should be redirected to dashboard");
    }

    /**
     * TC07: Verify Pod Lead / Pod Member Dashboard
     */
    @Test(priority = 7, description = "TC07: Verify Pod Lead Dashboard")
    public void testTC07_VerifyPodLeadDashboard() {
        Map<String, String> data = DataProviders.getTestCaseData("TC07_PodLead");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Pod Lead should be redirected to appropriate dashboard");
    }

    @Test(priority = 8, description = "TC07: Verify Pod Member Dashboard")
    public void testTC07_VerifyPodMemberDashboard() {
        Map<String, String> data = DataProviders.getTestCaseData("TC07_PodMember");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Pod Member should be redirected to appropriate dashboard");
    }

    /**
     * TC08: Verify Access to Protected Page Without Login
     */
    @Test(priority = 9, description = "TC08: Verify Access to Protected Page Without Login")
    public void testTC08_VerifyAccessToProtectedPageWithoutLogin() {
        Map<String, String> data = DataProviders.getTestCaseData("TC08");

        driver.get("https://project-lens-beta.vercel.app/dashboard");

        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageDisplayed() || driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")),
                "User should be prevented and redirected to /login");
    }

    /**
     * TC09: Verify Successful Logout
     */
    @Test(priority = 10, description = "TC09: Verify Successful Logout")
    public void testTC09_VerifySuccessfulLogout() {
        Map<String, String> data = DataProviders.getTestCaseData("TC09");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard should load before logout");

        dashboardPage.logout();
        Assert.assertTrue(loginPage.isLoginPageDisplayed() || driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")),
                "User should be logged out and redirected to login page");
    }

    /**
     * TC10: Verify Protected Page After Logout
     */
    @Test(priority = 11, description = "TC10: Verify Protected Page After Logout")
    public void testTC10_VerifyProtectedPageAfterLogout() {
        Map<String, String> data = DataProviders.getTestCaseData("TC10");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(data.get("Username"), data.get("Password"));

        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard should load");

        dashboardPage.logout();

        driver.navigate().back();

        Assert.assertTrue(loginPage.isLoginPageDisplayed() || driver.getCurrentUrl().contains(data.get("Expected_URL_Fraction")),
                "Protected page must not be accessible via Back button after logout");
    }
}
