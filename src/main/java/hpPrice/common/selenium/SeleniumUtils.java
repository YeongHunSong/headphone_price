package hpPrice.common.selenium;

import hpPrice.common.LoginInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static hpPrice.common.CommonConst.LOGIN_SLEEP_TIME;


@Slf4j
@Component
public class SeleniumUtils {

    private SeleniumUtils() {}

    public static Map<String, String> getNaverLoginCookies() {
        ChromeOptions options = new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("--start-maximized")
//                .addArguments("--disable-gpu")
//                .addArguments("--headless")
                .setExperimentalOption("detach", true);

        ChromeDriver driver = new ChromeDriver(options);

        try { // https://nid.naver.com/nidlogin.login?mode=form
            driver.get("https://nid.naver.com/nidlogin.login?mode=form");
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
            sleep(LOGIN_SLEEP_TIME);

            StringSelection loginId = new StringSelection(LoginInfo.LOGIN_ID);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(loginId, null);
            WebElement inputId = driver.findElement(By.className("input_id"));
            inputId.sendKeys(Keys.CONTROL + "v");
            sleep(LOGIN_SLEEP_TIME/2);

            inputId.sendKeys(Keys.TAB);
            sleep(LOGIN_SLEEP_TIME);

            StringSelection loginPw = new StringSelection(LoginInfo.LOGIN_PW);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(loginPw, null);
            WebElement inputPw = driver.findElement(By.className("input_pw"));
            inputPw.sendKeys(Keys.CONTROL + "v");
            sleep(LOGIN_SLEEP_TIME/2);

            inputPw.sendKeys(Keys.ENTER);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
            sleep(LOGIN_SLEEP_TIME);

            if (!driver.findElements(By.id("new.dontsave")).isEmpty()) {    // findElements -> 빈 리스트 반환
                driver.findElement(By.id("new.dontsave")).click();          // findElement -> 에러 발생
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
                sleep(LOGIN_SLEEP_TIME);
            }

//            sleep(1000000000); // 화면 확인용

            return driver.manage().getCookies()
                    .stream()
                    .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        } catch (InterruptedException e) {
            log.error("sleep error");
        }
        finally {
            driver.quit();
        }
        return null;
    }
}
