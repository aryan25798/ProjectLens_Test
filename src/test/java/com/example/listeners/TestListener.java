package com.example.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.example.utils.ConfigReader;
import com.example.utils.DriverManager;
import com.example.utils.ReportManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Map;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite() != null ? context.getSuite().getName() : "Test Suite";
        ReportManager.getInstance(suiteName);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String moduleName = resolveModuleName(className, contextOrSuiteName(result));

        String testDisplayName = buildTestDisplayName(result, moduleName);

        ExtentTest test = ReportManager.getInstance().createTest(testDisplayName);
        ReportManager.setTest(test);

        // Assign module category, author, and device for clear navigation tabs in HTML report
        test.assignCategory(moduleName);
        test.assignAuthor("QA Automation");
        String browser = ConfigReader.getProperty("browser");
        test.assignDevice(browser != null ? browser.toUpperCase() : "CHROME");

        // Log test start with Excel test parameters if present
        Object[] params = result.getParameters();
        if (params.length > 0 && params[0] instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> dataMap = (Map<String, String>) params[0];
            test.info("<b>Excel Test Data Inputs:</b><br/>" + buildParamTable(dataMap));
        } else {
            test.info("<b>Scenario:</b> " + (result.getMethod().getDescription() != null ? result.getMethod().getDescription() : result.getMethod().getMethodName()));
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ReportManager.getTest();
        if (test != null) {
            long duration = result.getEndMillis() - result.getStartMillis();
            test.log(Status.PASS, "<b>Remarks:</b> Test scenario executed successfully and all assertions passed. (Execution Time: " + (duration / 1000.0) + "s)");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ReportManager.getTest();
        if (test != null) {
            long duration = result.getEndMillis() - result.getStartMillis();
            String failureMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown assertion error";

            // Capture screenshot from active WebDriver and embed directly as Base64
            try {
                WebDriver driver = DriverManager.getDriver();
                if (driver != null) {
                    String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                    test.fail("<b>Failure Screenshot:</b>",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                }
            } catch (Exception e) {
                test.warning("Could not capture screenshot on failure: " + e.getMessage());
            }

            test.log(Status.FAIL, "<b>Remarks:</b> Test FAILED. Reason: " + failureMessage + " (Execution Time: " + (duration / 1000.0) + "s)");
            test.fail(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ReportManager.getTest();
        if (test != null) {
            String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Dependency / Precondition skipped";
            test.log(Status.SKIP, "<b>Remarks:</b> Test was skipped. Reason: " + reason);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ReportManager.flush();
    }

    private String resolveModuleName(String className, String fallback) {
        switch (className) {
            case "LoginTest":
                return "Login Module";
            case "TrainerReviewTest":
                return "Trainer Review Module";
            case "FormValidationTest":
                return "Form Validation Module";
            case "NotificationTest":
                return "Notification Module";
            default:
                return fallback != null && !fallback.isEmpty() ? fallback : "General Tests";
        }
    }

    private String contextOrSuiteName(ITestResult result) {
        try {
            return result.getTestContext().getName();
        } catch (Exception e) {
            return "Test Execution";
        }
    }

    private String buildTestDisplayName(ITestResult result, String moduleName) {
        Object[] params = result.getParameters();
        String desc = result.getMethod().getDescription();
        String methodName = result.getMethod().getMethodName();

        if (params.length > 0 && params[0] instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) params[0];
            String tcId = data.get("TC_ID");
            String scenario = data.get("Scenario");
            if (scenario == null || scenario.isEmpty()) {
                scenario = data.get("Test_Type");
            }
            if (scenario == null || scenario.isEmpty()) {
                scenario = data.get("Description");
            }

            if (tcId != null && !tcId.isEmpty()) {
                return "[" + moduleName + " | " + tcId + "] " + (scenario != null ? scenario : desc);
            }
        }

        if (desc != null && !desc.isEmpty()) {
            return "[" + moduleName + "] " + desc;
        }

        return "[" + moduleName + "] " + methodName;
    }

    private String buildParamTable(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='width:100%; border:1px solid #dee2e6; border-collapse:collapse; font-size:12px;'>");
        sb.append("<tr style='background-color:#f8f9fa;'><th style='padding:6px; border:1px solid #dee2e6; text-align:left; width:30%;'>Key</th><th style='padding:6px; border:1px solid #dee2e6; text-align:left;'>Value</th></tr>");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                sb.append("<tr>");
                sb.append("<td style='padding:4px 6px; border:1px solid #dee2e6; font-weight:600; color:#495057;'>").append(entry.getKey()).append("</td>");
                sb.append("<td style='padding:4px 6px; border:1px solid #dee2e6; color:#212529;'>").append(entry.getValue()).append("</td>");
                sb.append("</tr>");
            }
        }
        sb.append("</table>");
        return sb.toString();
    }
}

