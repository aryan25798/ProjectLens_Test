package com.example.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance(String suiteName) {
        if (extent == null) {
            File dir = new File(System.getProperty("user.dir"), "test-output");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File reportFile = new File(dir, "ExecutionReport.html");
            ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
            String title = (suiteName != null && !suiteName.isEmpty())
                    ? "ProjectLens - " + suiteName
                    : "ProjectLens Test Automation Report";
            spark.config().setDocumentTitle(title);
            spark.config().setReportName(title);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            spark.config().setCss(
                ":root { --primary-gradient: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); --badge-bg: #e2e8f0; } " +
                ".header { background: linear-gradient(90deg, #0f172a 0%, #1e293b 100%) !important; box-shadow: 0 4px 12px rgba(0,0,0,0.15); } " +
                ".nav-logo { font-weight: 800; font-size: 18px; color: #38bdf8 !important; letter-spacing: 0.5px; } " +
                ".nav-logo span { color: #f8fafc; font-weight: 500; } " +
                ".badge-category { font-size: 11px; padding: 4px 10px; border-radius: 20px; font-weight: 600; text-transform: uppercase; background: #e0f2fe !important; color: #0369a1 !important; border: 1px solid #bae6fd; } " +
                ".badge-author { font-size: 11px; padding: 4px 10px; border-radius: 20px; font-weight: 600; background: #fef3c7 !important; color: #b45309 !important; border: 1px solid #fde68a; } " +
                ".badge-device { font-size: 11px; padding: 4px 10px; border-radius: 20px; font-weight: 600; background: #f1f5f9 !important; color: #475569 !important; border: 1px solid #cbd5e1; } " +
                ".card { border-radius: 10px !important; box-shadow: 0 2px 10px rgba(0,0,0,0.05) !important; border: 1px solid #e2e8f0 !important; } " +
                ".table { border-collapse: separate; border-spacing: 0; } " +
                ".test-item { transition: all 0.2s ease; border-left: 4px solid transparent; } " +
                ".test-item:hover { transform: translateX(3px); background: #f8fafc; } " +
                ".test-status.pass { border-left-color: #22c55e !important; } " +
                ".test-status.fail { border-left-color: #ef4444 !important; } " +
                ".test-status.skip { border-left-color: #f59e0b !important; } " +
                ".test-detail { padding: 18px !important; } " +
                ".param-badge { display: inline-block; padding: 3px 8px; margin: 2px; border-radius: 6px; font-size: 11px; font-family: monospace; } " +
                ".status-pill-pass { background:#dcfce7; color:#15803d; padding:3px 10px; border-radius:12px; font-weight:bold; font-size:12px; display:inline-block; } " +
                ".status-pill-fail { background:#fee2e2; color:#b91c1c; padding:3px 10px; border-radius:12px; font-weight:bold; font-size:12px; display:inline-block; } " +
                ".status-pill-skip { background:#fef3c7; color:#b45309; padding:3px 10px; border-radius:12px; font-weight:bold; font-size:12px; display:inline-block; }"
            );

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Active Suite", (suiteName != null ? suiteName : "TestNG Suite"));
            extent.setSystemInfo("Application Under Test", "ProjectLens Platform");
            extent.setSystemInfo("Environment", "QA / Beta (Vercel)");
            extent.setSystemInfo("Application URL", ConfigReader.getProperty("app.baseUrl"));
            extent.setSystemInfo("Browser Engine", ConfigReader.getProperty("browser") != null ? ConfigReader.getProperty("browser").toUpperCase() : "CHROME");
            extent.setSystemInfo("Test Framework", "Selenium WebDriver 4 + TestNG");
            extent.setSystemInfo("Data-Driven Engine", "Apache POI (TestData.xlsx)");
            extent.setSystemInfo("User / Engineer", System.getProperty("user.name"));
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        return extent;
    }

    public static ExtentReports getInstance() {
        return getInstance(null);
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }

    public static void setTest(ExtentTest test) {
        testThread.set(test);
    }

    public static synchronized void flush() {
        if (extent != null) {
            try {
                extent.flush();
                File reportFile = new File(new File(System.getProperty("user.dir"), "test-output"), "ExecutionReport.html");
                System.out.println("[ProjectLens] HTML Test Report successfully written: " + reportFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Warning: Error flushing ExtentReports: " + e.getMessage());
            }
        }
    }

    public static synchronized void reset() {
        if (extent != null) {
            try {
                extent.flush();
            } catch (Exception ignored) {}
            extent = null;
        }
    }
}

