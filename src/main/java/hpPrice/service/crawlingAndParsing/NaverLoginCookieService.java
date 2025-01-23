package hpPrice.service.crawlingAndParsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hpPrice.common.dateTime.DateTimeUtils;
import hpPrice.common.naverCafe.LoginInfo;
import hpPrice.domain.LoginCookies;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static hpPrice.common.CommonConst.USER_AGENT;
import static hpPrice.common.CommonConst.LOGIN_SLEEP_TIME;
import static java.lang.Thread.sleep;


@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginCookieService {
    private final PostRepository postRepository;
    private final ObjectMapper objectMapper;

    public void storeLoginCookies(LoginCookies loginCookies) {
        postRepository.newLoginCookies(loginCookies);
    }

    public String latestLoginCookies(String desc) {
        return postRepository.findLatestLoginCookiesByDesc(desc);
    }


    public void setNaverLoginCookies() {
        log.info("네이버 로그인 쿠키 갱신 시작 [{}]", DateTimeUtils.getCurrentDateTime());

        ChromeOptions options = new ChromeOptions()
                .addArguments("--remote-allow-origins=*")
                .addArguments("--start-maximized")
                .addArguments("user-agent=" + USER_AGENT)
//                .addArguments("--disable-gpu")
//                .addArguments("--headless")
                .setExperimentalOption("detach", true); // 이걸 해야 창이 꺼짐;

        ChromeDriver driver = new ChromeDriver(options);

        try { // https://nid.naver.com/nidlogin.login?mode=form
            driver.get("https://nid.naver.com/nidlogin.login?url=https%3A%2F%2Fsection.cafe.naver.com%2Fca-fe%2Fhome%2Ffeed");
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

            if (!driver.findElements(By.id("new.dontsave")).isEmpty()) {    // findElements -> 빈 리스트 반환 (null 가능)
                driver.findElement(By.id("new.dontsave")).click();          // findElement -> null 시 에러 발생
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(LOGIN_SLEEP_TIME));
                sleep(LOGIN_SLEEP_TIME);
            }

            log.info("네이버 로그인 쿠키 갱신 완료 [{}]", DateTimeUtils.getCurrentDateTime());
//            return driver.manage().getCookies();

            storeLoginCookies(LoginCookies.newLoginCookies("naverLoginCookies",
                    objectMapper.writeValueAsString( // Map<String, String> 객체를 json 형태로 변환.
                            driver.manage().getCookies()
                                    .stream()
                                    .collect(Collectors.toMap(Cookie::getName, Cookie::getValue)))));

        } catch (InterruptedException | JsonProcessingException e) {
            log.error("Exception");
        } finally {
            driver.quit();
        }
    }

    public Map<String, String> getNaverLoginCookies() throws JsonProcessingException {
        return objectMapper.readValue(
                latestLoginCookies("naverLoginCookies"), new TypeReference<>() {}); // Map.class 변수 값에 따름.

    }
}
