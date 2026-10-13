package com.example.pages;

import com.example.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    // Locators
    private final By emailInput = By.cssSelector("input[type='email'], input[name='email'], form input:nth-of-type(1)");
    private final By passwordInput = By.cssSelector("input[name='password'], form div input");
    private final By showPasswordToggleBtn = By.cssSelector("form div button");
    private final By signInBtn = By.cssSelector("button.primary-button, form button[type='submit'], form > button");
    private final By errorMessageAlert = By.cssSelector(".alert.error, .alert, div[class*='error']");
    private final By formHeader = By.xpath("//h2[contains(text(),'Sign in to ProjectLens')]");
    private final By demoAccountsSection = By.cssSelector(".demo-accounts, div[class*='demo']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(emailInput) && isDisplayed(passwordInput) && isDisplayed(signInBtn);
    }

    public boolean isFormHeaderDisplayed() {
        return isDisplayed(formHeader);
    }

    public void enterEmail(String email) {
        if (email != null && !email.isEmpty()) {
            enterText(emailInput, email);
        }
    }

    public void enterPassword(String password) {
        if (password != null && !password.isEmpty()) {
            enterText(passwordInput, password);
        }
    }

    public void clickSignIn() {
        org.openqa.selenium.WebElement btn = WaitUtils.waitForElementClickable(driver, signInBtn, 10);
        try {
            btn.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", btn);
        }
    }

    public boolean isSignInButtonEnabled() {
        return driver.findElement(signInBtn).isEnabled();
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public String getPasswordInputType() {
        return getAttribute(passwordInput, "type");
    }

    public void toggleShowPassword() {
        click(showPasswordToggleBtn);
    }

    public String getErrorMessage() {
        try {
            return getText(errorMessageAlert);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isErrorMessageDisplayed() {
        for (int i = 0; i < 10; i++) {
            if (isDisplayed(errorMessageAlert)) {
                return true;
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        return false;
    }
}
