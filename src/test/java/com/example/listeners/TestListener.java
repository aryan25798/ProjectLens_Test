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
            test.info("<div style='margin-bottom:8px;'><span style='background:#0284c7; color:#fff; padding:3px 8px; border-radius:4px; font-size:11px; font-weight:600;'>DATA-DRIVEN INPUTS (Excel)</span></div>" + buildParamTable(dataMap));
        } else {
            String scenarioDesc = result.getMethod().getDescription() != null ? result.getMethod().getDescription() : result.getMethod().getMethodName();
            test.info("<div style='padding:8px 12px; background:#f8fafc; border-left:3px solid #0284c7; border-radius:4px;'><b>Scenario:</b> " + scenarioDesc + "</div>");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ReportManager.getTest();
        if (test != null) {
            long duration = result.getEndMillis() - result.getStartMillis();
            test.log(Status.PASS,
                "<div style='padding:10px; background:#f0fdf4; border:1px solid #bbf7d0; border-radius:6px;'>" +
                "<span class='status-pill-pass'>✓ PASSED</span> " +
                "<b>Execution Remarks:</b> All verification steps and assertions passed as expected." +
                "<div style='margin-top:6px; font-size:12px; color:#15803d;'>⏱ <b>Duration:</b> " + String.format("%.2f", duration / 1000.0) + "s</div>" +
                "</div>");
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
                    test.fail("<b>Defect Screenshot:</b>",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                }
            } catch (Exception e) {
                test.warning("Could not capture screenshot on failure: " + e.getMessage());
            }

            test.log(Status.FAIL,
                "<div style='padding:10px; background:#fef2f2; border:1px solid #fecaca; border-radius:6px;'>" +
                "<span class='status-pill-fail'>✕ FAILED</span> " +
                "<b>Defect Remarks:</b> " + (failureMessage != null ? failureMessage.replace("<", "&lt;").replace(">", "&gt;") : "Assertion failure") +
                "<div style='margin-top:6px; font-size:12px; color:#b91c1c;'>⏱ <b>Duration:</b> " + String.format("%.2f", duration / 1000.0) + "s</div>" +
                "</div>");
            test.fail(result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ReportManager.getTest();
        if (test != null) {
            String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Dependency / Precondition skipped";
            test.log(Status.SKIP,
                "<div style='padding:10px; background:#fffbeb; border:1px solid #fde68a; border-radius:6px;'>" +
                "<span class='status-pill-skip'>⚠ SKIPPED</span> " +
                "<b>Skip Remarks:</b> " + (reason != null ? reason.replace("<", "&lt;").replace(">", "&gt;") : "Skipped") +
                "</div>");
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
            case "ReviseSubmissionTest":
                return "Revise Submission Module";
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
        sb.append("<div style='overflow-x:auto; margin-top:4px;'>");
        sb.append("<table style='width:100%; border-collapse:collapse; font-size:12px; font-family:Inter,Segoe UI,sans-serif; background:#fff; border-radius:6px; overflow:hidden; border:1px solid #e2e8f0; box-shadow:0 1px 3px rgba(0,0,0,0.05);'>");
        sb.append("<tr style='background:linear-gradient(90deg, #1e293b, #334155); color:#f8fafc;'>")
          .append("<th style='padding:7px 12px; text-align:left; font-size:11px; text-transform:uppercase; letter-spacing:0.5px; width:28%;'>Parameter Key</th>")
          .append("<th style='padding:7px 12px; text-align:left; font-size:11px; text-transform:uppercase; letter-spacing:0.5px;'>Data Value</th>")
          .append("</tr>");
        int index = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                String bg = (index % 2 == 0) ? "#ffffff" : "#f8fafc";
                sb.append("<tr style='background:").append(bg).append("; border-bottom:1px solid #f1f5f9;'>");
                sb.append("<td style='padding:6px 12px; font-weight:600; color:#334155; border-right:1px solid #f1f5f9;'>").append(entry.getKey()).append("</td>");
                sb.append("<td style='padding:6px 12px; color:#0f172a; font-family:Consolas,monospace; font-size:11.5px;'>").append(entry.getValue().replace("<", "&lt;").replace(">", "&gt;")).append("</td>");
                sb.append("</tr>");
                index++;
            }
        }
        sb.append("</table></div>");
        return sb.toString();
    }
}

