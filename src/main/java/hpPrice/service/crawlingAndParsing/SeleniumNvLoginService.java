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
import org.openqa.selenium.*;
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
public class SeleniumNvLoginService {
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;


    public void updateNaverLoginCookies() {
        log.info("네이버 로그인 쿠키 갱신 시작 [{}]", DateTimeUtils.getCurrentDateTime());
        ChromeDriver driver = getChromeDriver();
        try {
            naverLoginScript(driver);
            avoidLoginSavePopup(driver);
            updateLoginCookies(driver);
            log.info("네이버 로그인 쿠키 갱신 완료 [{}]", DateTimeUtils.getCurrentDateTime());
        } catch (InterruptedException | JsonProcessingException e) {
            log.error("Exception -> ", e);
        } finally {
            driver.quit();
        }
    }

    private void naverLoginScript(ChromeDriver driver) throws InterruptedException { // headless 모드
        getDriverAndWait(driver, NV_LOGIN_URL);
        driver.executeScript(
                String.format("document.querySelector('input[id=\"id\"]').setAttribute('value', '%s')",
                        LoginInfo.LOGIN_ID));
        driver.executeScript(
                String.format("document.querySelector('input[id=\"pw\"]').setAttribute('value', '%s')",
                        LoginInfo.LOGIN_PW));
        driver.findElement(By.id("log.login")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
        sleep(SLEEP_TIME);
    }

    public Set<Cookie> getNaverLoginCookie() {
        try {
            return objectMapper.readValue(postRepository.findLatestLoginCookiesByDesc("naverLoginCookies"),
                            new TypeReference<Set<CookieConvert>>() {}).stream()
                    .map(CookieConvert::toSeleniumCookie)
                    .collect(Collectors.toSet());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException -> ", e);
        }
        throw new RuntimeException("-- getNaverLoginCookie failed --");
    }

    public ChromeDriver getChromeDriver() {
        return new ChromeDriver(new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("--user-agent=" + USER_AGENT)
                .addArguments("--headless") /// 아래 모두 headless 설정
                .addArguments("--no-sandbox")
                .addArguments("--window-size=1920x1080")
                .addArguments("--disable-gpu"));
    }

    public void getDriverAndWait(ChromeDriver driver, String url) {
        for (int tryCount = 0; tryCount < MAX_RETRY_COUNT; tryCount++) {
            try {
                sleep(SLEEP_TIME);
                driver.get(url);
                driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(TIME_OUT));
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
                sleep(SLEEP_TIME);
                return;
            } catch (TimeoutException | InterruptedException e) {
                log.info("timeout -> refresh");
                log.error("TimeoutException -> ", e);
                driver.navigate().refresh();
            }
        }
        driver.quit();
        throw new RuntimeException("페이지 로드 실패");
    }

    private void avoidLoginSavePopup(ChromeDriver driver) throws InterruptedException {
        if (!driver.findElements(By.id("new.dontsave")).isEmpty()) {    // findElements -> 빈 리스트 반환 (null 가능)
            driver.findElement(By.id("new.dontsave")).click();          // findElement -> null 시 에러 발생
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
            sleep(SLEEP_TIME);
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



    /// Deprecated Method

    @Deprecated
    private void naverLoginJava(ChromeDriver driver) { // headless 모드에서는 클립보드 접근이 불가능해 이 방식으로는 로그인 불가능
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(LoginInfo.LOGIN_ID), null);
        WebElement inputLoginId = driver.findElement(By.className("input_id"));
        inputLoginId.sendKeys(Keys.CONTROL + "v");
        inputLoginId.sendKeys(Keys.TAB);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(LoginInfo.LOGIN_PW), null);
        WebElement inputLoginPw = driver.findElement(By.className("input_pw"));
        inputLoginPw.sendKeys(Keys.CONTROL + "v");
        inputLoginPw.sendKeys(Keys.ENTER);

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
    }

    @Deprecated
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

    @Deprecated
    public Map<String, String> getCookieToJsoup() throws JsonProcessingException {
        return objectMapper.readValue(
                postRepository.findLatestLoginCookiesByDesc("naverLoginCookies"), new TypeReference<>() {});
    }
}
