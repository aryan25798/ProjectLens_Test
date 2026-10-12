# testing-pl

Industry-grade **Maven Page Object Model (POM)** test automation framework setup matching the Cognizant enterprise standard.

## Directory Layout
- `src/main/java/com/example/pages/`: Page Object Model classes (`BasePage.java`, `LoginPage.java`, etc.)
- `src/main/java/com/example/utils/`: Helper utilities (`DriverManager.java`, `ConfigReader.java`, `WaitUtils.java`, `ReportManager.java`, `ExcelReader.java`)
- `src/test/java/com/example/base/`: Test setup and teardown hooks (`BaseTest.java` with `@BeforeMethod`, `@AfterMethod`)
- `src/test/java/com/example/tests/`: Test cases grouped by module (`LoginTest.java`, etc.)
- `src/test/java/com/example/listeners/`: Test execution listeners (`TestListener.java` for screenshots and logging)
- `src/test/resources/config/`: Configuration files (`config.properties`, `env-dev.properties`)
- `src/test/resources/testdata/`: Test data assets (`TestData.xlsx`, `users.json`, etc.)
- `src/test/resources/log4j2.xml`: Logger configuration

## Execution Options

### Option 1: Using Maven Profiles (Recommended for All Laptops & Terminals)
No quotes, dots, or escaping needed. Works identically in Windows PowerShell, CMD, Git Bash, Mac, and Linux:

* **All Modules (Full Suite):**
  ```bash
  mvn test
  # or
  mvn test -Pall
  ```
* **Login Module Only:**
  ```bash
  mvn test -Plogin
  ```
* **Trainer Review Module Only:**
  ```bash
  mvn test -Ptrainer
  ```
* **Form Validation Module Only:**
  ```bash
  mvn test -Pform
  ```
* **Notification Module Only:**
  ```bash
  mvn test -Pnotifications
  ```
* **Revise Submission Module Only (TC01 to TC06):**
  ```bash
  mvn test -Previse
  ```

---

### Option 2: 1-Click Batch Runners (Windows)
Double-click or run from terminal in project root:
* `run-all.bat` - Runs full master test suite
* `run-login.bat` - Runs Login module only
* `run-trainer.bat` - Runs Trainer Review module only
* `run-form.bat` - Runs Form Validation module only
* `run-notifications.bat` - Runs Notification module only
* `run-revise.bat` - Runs Revise Submission module only

---

### Option 3: Dynamic Suite Property (`-DsuiteXmlFile`)
* **Windows PowerShell:**
  ```powershell
  mvn test "-DsuiteXmlFile=testng-trainer.xml"
  ```
* **CMD / Bash:**
  ```bash
  mvn test -DsuiteXmlFile=testng-trainer.xml
  ```

## Test Reports & Remarks
After test execution, comprehensive reports with pass/fail status, execution time, and failure remarks/screenshots are generated at:
- **Interactive HTML Report:** `test-output/ExecutionReport.html` (open in any web browser)
- **Maven Surefire Summary:** `target/surefire-reports/emailable-report.html`


