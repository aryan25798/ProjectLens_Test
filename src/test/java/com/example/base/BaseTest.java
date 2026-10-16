package com.example.base;

import com.example.listeners.TestListener;
import com.example.utils.ConfigReader;
import com.example.utils.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        DriverManager.initDriver();
        driver = DriverManager.getDriver();
        String baseUrl = ConfigReader.getProperty("app.baseUrl");
        driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
