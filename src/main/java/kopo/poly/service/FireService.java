package kopo.poly.service;

import kopo.poly.dto.FireDTO;
import kopo.poly.persistance.mapper.IFireMapper;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class FireService {

    private final IFireMapper fireMapper;

    @Scheduled(cron = "0 1 * * * *") // 매 시간 1분마다 크롤링하게 스케줄러 설정함!!! (초,분,시,일,월,요일 순)
    public void insertFireInfo() throws Exception {

        log.info(this.getClass().getName() + ".insertFireInfo 서비스 시작!");

        log.info("기존에 수집된 산불 정보 데이터를 삭제합니다.");

        fireMapper.deleteFireInfo(); // 기존에 수집된 산불 정보 데이터 삭제하기

        log.info("삭제 완료!");

        log.info("셀레니움으로 크롤링 가보자고~~!!");

        System.setProperty("webdriver.chrome.driver", "/chromedriver-win32/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("headless");
        options.addArguments("--whitelisted-ips=127.0.0.1"); // 로컬 IP를 화이트리스트에 추가하는 거
        options.addArguments("--remote-allow-origins=*"); // 모든 원격 호스트로부터의 요청을 허용

        WebDriver webDriver = new ChromeDriver(options);

        String url = "http://forestfire.nifos.go.kr/menu.action?menuNum=1";

        webDriver.get(url);

        Duration timeout = Duration.ofSeconds(10);
        WebDriverWait wait = new WebDriverWait(webDriver, timeout);

        log.info("셀레니움 준비 완!");

        //String collectTime = DateUtil.getDateTime("yyyyMMdd"); // 수집날짜 = 오늘날짜
        String collectTime = DateUtil.getDateTime("yyyyMMddHHmm"); // 수집시간 = 현재시간

        log.info("수집시간 : " + collectTime);

        int i = 2; // 시작값 설정!!! 2부터 전국 값 가져오기 시작함

        while (true) {

            try {

                String city = webDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/div[3]/div[1]/div[2]/div[4]/div/div/ul/li[" + i + "]/div/p[1]")).getText();
                String grade = webDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/div[3]/div[1]/div[2]/div[4]/div/div/ul/li[" + i + "]/div/p[2]/img")).getAttribute("alt");
                String score = webDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div[1]/div[3]/div[1]/div[2]/div[4]/div/div/ul/li[" + i + "]/div/p[3]")).getText();

                log.info("------------------------");
                log.info("지역: " + city);
                log.info("등급: " + grade);
                log.info("지수: " + score);

                FireDTO pDTO = new FireDTO();
                pDTO.setCollectTime(collectTime); // 수집날짜 담기!!
                pDTO.setCity(city);
                pDTO.setGrade(grade);
                pDTO.setScore(score);

                fireMapper.insertFireInfo(pDTO);

                pDTO = null;

                i++;

            } catch (NoSuchElementException e) {
                break;
            }

        }

        // 웹 드라이버 종료
        webDriver.quit();

    }
}
