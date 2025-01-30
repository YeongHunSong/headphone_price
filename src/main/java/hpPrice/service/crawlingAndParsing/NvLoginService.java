package hpPrice.service.crawlingAndParsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.common.naverCafe.CookieConvert;
import hpPrice.common.naverCafe.LoginInfo;
import hpPrice.domain.naver.LoginCookies;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static hpPrice.common.CommonConst.*;
import static java.lang.Thread.sleep;


@Slf4j
@Service
@RequiredArgsConstructor
public class NvLoginService {
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;


    public void updateNaverLoginCookies() {
        log.info("네이버 로그인 쿠키 갱신 시작 [{}]", DateTimeUtils.getCurrentDateTime());
        ChromeDriver driver = getChromeDriver();
        try {
            driverGetAndWait(driver, NV_LOGIN_URL);

            naverLogin(driver);
            avoidLoginSavePopup(driver);
            updateLoginCookies(driver);

            log.info("네이버 로그인 쿠키 갱신 완료 [{}]", DateTimeUtils.getCurrentDateTime());
        } catch (InterruptedException | JsonProcessingException e) {
            log.error("Exception", e);
        } finally {
            driver.quit();
        }
    }

    public ChromeDriver getChromeDriver() {
        return new ChromeDriver(new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("user-agent=" + USER_AGENT));
    }

    public void driverGetAndWait(ChromeDriver driver, String url) throws InterruptedException {
        sleep(SLEEP_TIME);
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
        sleep(SLEEP_TIME);
    }


    public Set<Cookie> getNaverLoginCookie() {
        try {
            return objectMapper.readValue(postRepository.findLatestLoginCookiesByDesc("naverLoginCookies")
                            , new TypeReference<Set<CookieConvert>>() {})
                    .stream()
                    .map(CookieConvert::toSeleniumCookie)
                    .collect(Collectors.toSet());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException -> ", e);
        }
        throw new RuntimeException("-- getNaverLoginCookie failed --");
    }

    private void naverLogin(ChromeDriver driver) throws InterruptedException {
        inputLoginData(driver, LoginInfo.LOGIN_ID, "input_id", Keys.TAB);
        inputLoginData(driver, LoginInfo.LOGIN_PW, "input_pw", Keys.ENTER);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME/2));
    }

    private void inputLoginData(ChromeDriver driver, String inputData, String inputClassName, Keys nextKey) throws InterruptedException {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(inputData), null);
        WebElement inputId = driver.findElement(By.className(inputClassName));
        inputId.sendKeys(Keys.CONTROL + "v");
        sleep(LOGIN_SLEEP_TIME/4);

        inputId.sendKeys(nextKey);
        sleep(LOGIN_SLEEP_TIME/2);
    }

    private void avoidLoginSavePopup(ChromeDriver driver) {
        if (!driver.findElements(By.id("new.dontsave")).isEmpty()) {    // findElements -> 빈 리스트 반환 (null 가능)
            driver.findElement(By.id("new.dontsave")).click();          // findElement -> null 시 에러 발생
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
        }
    }

    private void updateLoginCookies(ChromeDriver driver) throws JsonProcessingException {
        Set<CookieConvert> convertCookies = driver.manage().getCookies()
                .stream()
                .map(CookieConvert::new)
                .collect(Collectors.toSet());

        postRepository.newLoginCookies(
                LoginCookies.newLoginCookies("naverLoginCookies",
                        objectMapper.writeValueAsString(convertCookies)));
    }



    private String convertJson(Set<Cookie> seleniumCookies) throws JsonProcessingException {
        Map<String, String> cookieMap = seleniumCookies.stream()
                .collect(Collectors.toMap(
                        Cookie::getName,
                        Cookie::getValue));
        cookieMap.put("domain", ".naver.com");
//        cookieMap.put("expiry", "Sun Jan 26 15:10:56 KST 2025");
        cookieMap.put("path", "=/");
        cookieMap.put("sameSite", "Lax");
        return objectMapper.writeValueAsString(cookieMap);
    }

    public Map<String, String> getCookieToJsoup() throws JsonProcessingException {
        return objectMapper.readValue(
                postRepository.findLatestLoginCookiesByDesc("naverLoginCookies"), new TypeReference<>() {});
    }
}
