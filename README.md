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

## Execution
Run all tests via Master TestNG Suite:
```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```

Run modules individually:
- **Login Flow:**
  ```bash
  mvn test -Dsurefire.suiteXmlFiles=testng-login.xml
  ```
- **Trainer Review Flow:**
  ```bash
  mvn test -Dsurefire.suiteXmlFiles=testng-trainer.xml
  ```
- **Form Validation Workflow (PL-04 to PL-07):**
  ```bash
  mvn test -Dsurefire.suiteXmlFiles=testng-form-validation.xml
  ```
- **Notification Workflow (TC_NOTIF_01 to TC_NOTIF_07):**
  ```bash
  mvn test -Dsurefire.suiteXmlFiles=testng-notifications.xml
  ```

> [!NOTE]
> **Windows PowerShell Users:** Wrap system properties with quotes to avoid dot-parsing errors:
> ```powershell
> mvn test "-Dsurefire.suiteXmlFiles=testng-notifications.xml"
> ```

## Test Reports & Remarks
After test execution, comprehensive reports with pass/fail status, execution time, and failure remarks/screenshots are generated at:
- **Interactive HTML Report:** `test-output/ExecutionReport.html` (open in any web browser)
- **Maven Surefire Summary:** `target/surefire-reports/emailable-report.html`


