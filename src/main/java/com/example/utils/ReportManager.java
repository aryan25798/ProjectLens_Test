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
            String reportDir = "test-output";
            File dir = new File(reportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String reportPath = reportDir + "/ExecutionReport.html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            String title = (suiteName != null && !suiteName.isEmpty())
                    ? "ProjectLens - " + suiteName
                    : "ProjectLens Test Automation Report";
            spark.config().setDocumentTitle(title);
            spark.config().setReportName(title);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            spark.config().setCss(
                ".header { background-color: #1a365d !important; } " +
                ".nav-logo { font-weight: bold; font-size: 16px; color: #fff; } " +
                ".badge-category { font-size: 11px; padding: 4px 8px; border-radius: 4px; }"
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

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}

