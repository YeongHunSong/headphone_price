package hpPrice.common.selenium;

import hpPrice.common.LoginInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;

import static java.lang.Thread.sleep;
import static hpPrice.common.CommonConst.LOGIN_SLEEP_TIME;


@Slf4j
@Component
public class SeleniumUtils {

    private SeleniumUtils() {}

    public static void loginNaver() {
        ChromeOptions options = new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("--start-maximized")
                .setExperimentalOption("detach", true);

        ChromeDriver driver = new ChromeDriver(options);

        try { // https://nid.naver.com/nidlogin.login?mode=form&svctype=262144
            driver.get("https://nid.naver.com/nidlogin.login?mode=form");
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
            sleep(LOGIN_SLEEP_TIME);

            StringSelection loginId = new StringSelection(LoginInfo.LOGIN_ID);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(loginId, null);
            WebElement inputId = driver.findElement(By.className("input_id"));
            inputId.sendKeys(Keys.CONTROL + "v");
            sleep(LOGIN_SLEEP_TIME);

            inputId.sendKeys(Keys.TAB);
            sleep(LOGIN_SLEEP_TIME);

            StringSelection loginPw = new StringSelection(LoginInfo.LOGIN_PW);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(loginPw, null);
            WebElement inputPw = driver.findElement(By.className("input_pw"));
            inputPw.sendKeys(Keys.CONTROL + "v");
            sleep(LOGIN_SLEEP_TIME);

            inputPw.sendKeys(Keys.ENTER);
            sleep(1000000000); // 확인용

        } catch (InterruptedException e) {
            log.error("sleep error");
        }
        finally {
            driver.quit();
        }
    }
}
