package hpPrice;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public static class NaverCrawler {
		public static void main(String[] args) {
			// WebDriver 객체 생성
			WebDriver driver = new ChromeDriver();

			try {
				// 네이버 홈페이지로 이동
				String url = "https://www.naver.com";
				driver.get(url);

				// 네이버 페이지에서 특정 요소 가져오기 (예: 주요 메뉴 제목들)
				List<WebElement> menuElements = driver.findElements(By.cssSelector(".nav"));

				System.out.println("===== Naver Main Menus =====");
				for (WebElement element : menuElements) {
					System.out.println(element.getText());
				}

				// 추가적으로 검색창에 텍스트 입력과 검색 버튼 클릭 예제
				WebElement searchBox = driver.findElement(By.id("query")); // 검색창의 ID
				searchBox.sendKeys("Selenium WebDriver");

				WebElement searchButton = driver.findElement(By.className("btn_search")); // 검색 버튼의 클래스명
				searchButton.click();

				// 결과 페이지에서 출력
				List<WebElement> resultElements = driver.findElements(By.cssSelector(".lnb")); // 결과 일부를 추출
				for (WebElement result : resultElements) {
					System.out.println(result.getText());
				}

			} catch (Exception e) {
				e.printStackTrace(); // 오류 로그 출력
			} finally {
				// 드라이버 종료
				driver.quit();
			}
		}
	}
}
