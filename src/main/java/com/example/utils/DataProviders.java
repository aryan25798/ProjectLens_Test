package com.example.utils;

import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Map;

public class DataProviders {

    private static final String EXCEL_PATH = ConfigReader.getProperty("excel.testdata.path");

    /**
     * DataProvider feeding BOTH Valid and Invalid login combinations directly from Excel.
     */
    @DataProvider(name = "loginCredentialsDDT")
    public static Object[][] getLoginCredentialsData() {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        List<Map<String, String>> data = excel.getData("LoginCredentialsData");

        Object[][] testData = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }
        return testData;
    }

    /**
     * Retrieve single test case definitions by TC_ID from Excel.
     */
    public static Map<String, String> getTestCaseData(String tcId) {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        return excel.getTestCaseData("TestCaseDefinitions", tcId);
    }

    /**
     * DataProvider for Trainer Review Workflow from TrainerReviewWorkflow sheet.
     */
    @DataProvider(name = "trainerReviewDDT")
    public static Object[][] getTrainerReviewData() {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        List<Map<String, String>> data = excel.getData("TrainerReviewWorkflow");

        Object[][] testData = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }
        return testData;
    }

    /**
     * Get specific Trainer Review test case data from Excel.
     */
    public static Map<String, String> getTrainerReviewTestCaseData(String tcId) {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        return excel.getTestCaseData("TrainerReviewWorkflow", tcId);
    }

    /**
     * DataProvider for Form Validation Workflow from FormValidationWorkflow sheet.
     */
    @DataProvider(name = "formValidationDDT")
    public static Object[][] getFormValidationData() {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        List<Map<String, String>> data = excel.getData("FormValidationWorkflow");

        Object[][] testData = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }
        return testData;
    }

    /**
     * Get specific Form Validation test case data from Excel.
     */
    public static Map<String, String> getFormValidationTestCaseData(String tcId) {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        return excel.getTestCaseData("FormValidationWorkflow", tcId);
    }

    /**
     * DataProvider for Notification Workflow from NotificationWorkflow sheet.
     */
    @DataProvider(name = "notificationDDT")
    public static Object[][] getNotificationData() {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        List<Map<String, String>> data = excel.getData("NotificationWorkflow");

        Object[][] testData = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            testData[i][0] = data.get(i);
        }
        return testData;
    }

    /**
     * Get specific Notification test case data from Excel.
     */
    public static Map<String, String> getNotificationTestCaseData(String tcId) {
        ExcelReader excel = new ExcelReader(EXCEL_PATH);
        return excel.getTestCaseData("NotificationWorkflow", tcId);
    }
}
