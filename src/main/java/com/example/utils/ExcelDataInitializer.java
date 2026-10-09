package com.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelDataInitializer {

    public static void main(String[] args) {
        String filePath = "src/test/resources/testdata/TestData.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {

            // ==========================================
            // SHEET 1: Data-Driven Login Scenarios (Valid & Invalid Matrix)
            // ==========================================
            Sheet ddtSheet = workbook.createSheet("LoginCredentialsData");

            Row ddtHeader = ddtSheet.createRow(0);
            String[] ddtHeaders = {
                "Test_Type",
                "Username",
                "Password",
                "Expected_Result",
                "Expected_Error_Message"
            };
            for (int i = 0; i < ddtHeaders.length; i++) {
                Cell c = ddtHeader.createCell(i);
                c.setCellValue(ddtHeaders[i]);
            }

            String[][] ddtRows = {
                { "VALID", "trainer@projectlens.local", "Trainer@123", "SUCCESS", "" },
                { "VALID", "alpha@projectlens.local", "PodLead@123", "SUCCESS", "" },
                { "VALID", "alpha.member@projectlens.local", "Member@123", "SUCCESS", "" },
                { "INVALID", "trainer@projectlens.local", "WrongPassword!99", "FAILURE", "Invalid email or password." },
                { "INVALID", "alpha@projectlens.local", "Incorrect#123", "FAILURE", "Invalid email or password." },
                { "INVALID", "unknown.user@projectlens.local", "Trainer@123", "FAILURE", "Invalid email or password." },
                { "INVALID", "random.tester@example.com", "Password@123", "FAILURE", "Invalid email or password." },
                { "INVALID", "admin'--@projectlens.local", "Password123", "FAILURE", "Invalid email or password." }
            };

            for (int r = 0; r < ddtRows.length; r++) {
                Row row = ddtSheet.createRow(r + 1);
                for (int c = 0; c < ddtRows[r].length; c++) {
                    row.createCell(c).setCellValue(ddtRows[r][c]);
                }
            }
            for (int i = 0; i < ddtHeaders.length; i++) {
                ddtSheet.autoSizeColumn(i);
            }

            // ==========================================
            // SHEET 2: Login Test Case Definitions (TC01 to TC10)
            // ==========================================
            Sheet tcSheet = workbook.createSheet("TestCaseDefinitions");

            Row tcHeader = tcSheet.createRow(0);
            String[] tcHeaders = {
                "TC_ID",
                "Scenario",
                "Username",
                "Password",
                "Expected_Result",
                "Expected_URL_Fraction"
            };
            for (int i = 0; i < tcHeaders.length; i++) {
                tcHeader.createCell(i).setCellValue(tcHeaders[i]);
            }

            String[][] tcData = {
                { "TC01", "Verify Login Page Display", "", "", "Login page displayed with required fields", "/login" },
                { "TC02", "Verify Login with Valid Credentials", "trainer@projectlens.local", "Trainer@123", "User authenticated successfully", "dashboard" },
                { "TC03", "Verify Login with Invalid Credentials", "invalid.user@projectlens.local", "WrongPassword!123", "Login unsuccessful and error message displayed", "/login" },
                { "TC04", "Verify Mandatory Field Validation", "", "", "Sign in button disabled / validation displayed", "/login" },
                { "TC05", "Verify Password Masking", "", "Trainer@123", "Password input masked with type=password and toggles to text", "/login" },
                { "TC06", "Verify Trainer Role Dashboard", "trainer@projectlens.local", "Trainer@123", "Trainer redirected to Trainer Dashboard", "dashboard" },
                { "TC07_PodLead", "Verify Pod Lead Dashboard", "alpha@projectlens.local", "PodLead@123", "Pod Lead redirected to appropriate dashboard", "dashboard" },
                { "TC07_PodMember", "Verify Pod Member Dashboard", "alpha.member@projectlens.local", "Member@123", "Pod Member redirected to appropriate dashboard", "dashboard" },
                { "TC08", "Verify Access to Protected Page Without Login", "", "", "User prevented from accessing dashboard and redirected to login", "/login" },
                { "TC09", "Verify Successful Logout", "trainer@projectlens.local", "Trainer@123", "User logged out successfully and redirected to login", "/login" },
                { "TC10", "Verify Protected Page After Logout", "trainer@projectlens.local", "Trainer@123", "Protected page not accessible via back button after logout", "/login" }
            };

            for (int r = 0; r < tcData.length; r++) {
                Row row = tcSheet.createRow(r + 1);
                for (int c = 0; c < tcData[r].length; c++) {
                    row.createCell(c).setCellValue(tcData[r][c]);
                }
            }
            for (int i = 0; i < tcHeaders.length; i++) {
                tcSheet.autoSizeColumn(i);
            }

            // ==========================================
            // SHEET 3: Trainer Review Workflow (TC01, TC02, TC04, TC05, TC06)
            // Excluding TC03 Overlap Detection as requested
            // ==========================================
            Sheet trainerSheet = workbook.createSheet("TrainerReviewWorkflow");

            Row trHeader = trainerSheet.createRow(0);
            String[] trHeaders = {
                "TC_ID",
                "Scenario",
                "Trainer_Email",
                "Trainer_Password",
                "Submission_Title",
                "Decision_Action",
                "Comments",
                "Expected_Queue_State",
                "Expected_Status"
            };
            for (int i = 0; i < trHeaders.length; i++) {
                trHeader.createCell(i).setCellValue(trHeaders[i]);
            }

            String[][] trData = {
                {
                    "TC01",
                    "Verify Navigation to Reviews Queue",
                    "trainer@projectlens.local",
                    "Trainer@123",
                    "",
                    "",
                    "",
                    "Decision queue loads, displaying queue items ready for review",
                    "/reviews"
                },
                {
                    "TC02",
                    "Verify Submission Details Display",
                    "trainer@projectlens.local",
                    "Trainer@123",
                    "DefectSense AI",
                    "",
                    "",
                    "Details panel loaded displaying title, pod details, alignment score, and problem statement",
                    "/reviews"
                },
                {
                    "TC04",
                    "Verify Successful Submission Approval",
                    "trainer@projectlens.local",
                    "Trainer@123",
                    "DefectSense AI",
                    "Approve",
                    "Excellent idea and complete tech stack coverage. Approved for development.",
                    "Removed from active pending queue, submission marked as Approved",
                    "APPROVED"
                },
                {
                    "TC05",
                    "Verify Requesting Revision",
                    "trainer@projectlens.local",
                    "Trainer@123",
                    "DefectSense AI",
                    "Needs Revision",
                    "Please clarify automation test framework and integration details.",
                    "Status updates to Needs Revision, feedback saved",
                    "NEEDS_REVISION"
                },
                {
                    "TC06",
                    "Verify Rejection Flow",
                    "trainer@projectlens.local",
                    "Trainer@123",
                    "DefectSense AI",
                    "Reject",
                    "Criteria threshold not met and project scope is redundant.",
                    "Status updates to Rejected, decision finalized",
                    "REJECTED"
                }
            };

            for (int r = 0; r < trData.length; r++) {
                Row row = trainerSheet.createRow(r + 1);
                for (int c = 0; c < trData[r].length; c++) {
                    row.createCell(c).setCellValue(trData[r][c]);
                }
            }
            for (int i = 0; i < trHeaders.length; i++) {
                trainerSheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("TestData.xlsx successfully generated with 3 sheets at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
