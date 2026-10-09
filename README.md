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
Run tests via Maven:
```bash
mvn clean test
```
Or execute specific TestNG suite:
```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```
