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
            // ==========================================
            // SHEET 4: Form Validation Workflow (PL-04, PL-05, PL-06, PL-07)
            // ==========================================
            Sheet formValSheet = workbook.createSheet("FormValidationWorkflow");

            Row fvHeader = formValSheet.createRow(0);
            String[] fvHeaders = {
                "TC_ID",
                "Scenario",
                "User_Email",
                "User_Password",
                "Project_Title",
                "Problem_Statement",
                "Objectives",
                "Tech_Stack",
                "Doc_Link",
                "Expected_Result",
                "Expected_Status",
                "Expected_Cohort_Title",
                "Expected_Min_Alignment"
            };
            for (int i = 0; i < fvHeaders.length; i++) {
                fvHeader.createCell(i).setCellValue(fvHeaders[i]);
            }

            String[][] fvData = {
                {
                    "PL-04",
                    "Open the new project submission page",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "The form opens for the Pod Lead and displays the active cohort criteria and the 70% minimum alignment requirement.",
                    "/submit",
                    "AI-Assisted Engineering",
                    "70%"
                },
                {
                    "PL-05",
                    "Submit the form with required information missing",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "Project title, problem statement, objectives and technology stack are validated. Submission is blocked until the required fields are completed; the documentation link remains optional.",
                    "Please complete all required fields.",
                    "",
                    ""
                },
                {
                    "PL-06",
                    "Submit a complete project idea",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "CloudAuto QA Platform",
                    "Software engineering teams face continuous delivery delays due to slow manual QA testing cycles. This platform automates regression workflows and provides real-time failure insights.",
                    "1. Automate 100% of critical core business workflows. 2. Integrate continuous feedback and defect detection. 3. Reduce manual regression effort by 60%.",
                    "Java, Spring Boot, Angular, REST API",
                    "https://github.com/projectlens/cloudauto-qa",
                    "The submission is saved, evaluation is initiated and the project appears in the Pod Lead's submissions list with its current status.",
                    "/submissions",
                    "",
                    ""
                },
                {
                    "PL-07",
                    "Verify a project evaluated below 70% alignment",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "The project is marked Needs Revision and the result clearly shows the score, analysis summary, matched criteria and missing criteria.",
                    "NEEDS_REVISION",
                    "",
                    "70%"
                }
            };

            for (int r = 0; r < fvData.length; r++) {
                Row row = formValSheet.createRow(r + 1);
                for (int c = 0; c < fvData[r].length; c++) {
                    row.createCell(c).setCellValue(fvData[r][c]);
                }
            }
            for (int i = 0; i < fvHeaders.length; i++) {
                formValSheet.autoSizeColumn(i);
            }

            // ==========================================
            // SHEET 5: Notification Module Workflow (TC_NOTIF_01 to TC_NOTIF_07)
            // ==========================================
            Sheet notifSheet = workbook.createSheet("NotificationWorkflow");

            Row notifHeader = notifSheet.createRow(0);
            String[] notifHeaders = {
                "TC_ID",
                "Scenario",
                "User_Email",
                "User_Password",
                "Expected_Result",
                "Expected_URL_Fraction",
                "Expected_Alert_Keyword",
                "Assigned_Pod"
            };
            for (int i = 0; i < notifHeaders.length; i++) {
                notifHeader.createCell(i).setCellValue(notifHeaders[i]);
            }

            String[][] notifData = {
                {
                    "TC_NOTIF_01",
                    "Open Notifications Page",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Notifications page opens and displays a chronological list of recent updates with date and time.",
                    "/notifications",
                    "Notifications",
                    "Pod Alpha"
                },
                {
                    "TC_NOTIF_02",
                    "Low Score Alert (< 70%)",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Alert displays: Your project idea '[Idea Name]' needs improvement. Alignment score: [X]%. Please revise and reupload.",
                    "/notifications",
                    "needs improvement. Alignment score:",
                    "Pod Alpha"
                },
                {
                    "TC_NOTIF_03",
                    "High Score Alert (>= 70%)",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Alert displays: Your project idea '[Idea Name]' qualified for trainer review with an alignment score of [X]%.",
                    "/notifications",
                    "qualified for trainer review with an alignment score of",
                    "Pod Alpha"
                },
                {
                    "TC_NOTIF_04",
                    "Trainer Decision Alert",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Alert displays trainer's decision, e.g., Trainer decision for '[Idea Name]': Approved.",
                    "/notifications",
                    "Trainer decision for",
                    "Pod Alpha"
                },
                {
                    "TC_NOTIF_05",
                    "Mark Notification as Read",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Notification status updates to 'Read'.",
                    "/notifications",
                    "Read",
                    "Pod Alpha"
                },
                {
                    "TC_NOTIF_06",
                    "Refresh Notifications List",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Page refreshes and fetches the latest notifications.",
                    "/notifications",
                    "Refresh",
                    "Pod Alpha"
                },
                {
                    "TC_NOTIF_07",
                    "Pod Isolation Check",
                    "alpha@projectlens.local",
                    "PodLead@123",
                    "Pod Lead only sees notifications belonging to their assigned Pod and zero notifications from other pods.",
                    "/notifications",
                    "Pod Alpha",
                    "Pod Alpha"
                }
            };

            for (int r = 0; r < notifData.length; r++) {
                Row row = notifSheet.createRow(r + 1);
                for (int c = 0; c < notifData[r].length; c++) {
                    row.createCell(c).setCellValue(notifData[r][c]);
                }
            }
            for (int i = 0; i < notifHeaders.length; i++) {
                notifSheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("TestData.xlsx successfully generated with 5 sheets at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
