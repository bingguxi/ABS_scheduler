package kopo.poly.service;

import kopo.poly.dto.DustDTO;
import kopo.poly.persistance.mapper.IDustMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
@Service
public class DustService {

    private final IDustMapper dustMapper;

    @Scheduled(cron = "0 1 0 * * *") // 매일 오전 12시 1분 0초마다 실행되게 스케줄러 설정
    @Transactional
    public void manageDustInfo() throws Exception {

        log.info(this.getClass().getName() + ".manageDustInfo Start!");

        log.info("DB 삭제 시작");

        dustMapper.deleteDustInfo();

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 날짜 및 시간 형식을 설정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        // 현재 시간을 원하는 형식으로 변환합니다.
        String formattedDate = dateFormat.format(currentDate);

        // 변환된 날짜와 시간을 사용하여 URL을 생성합니다.
        String url = "https://apihub.kma.go.kr/api/typ01/url/dst_pm10_hr.php?tm=" + formattedDate + "&help=0&org=%27kma%27&authKey=0a-KCqUxRzuvigqlMbc7rg";
        String URL = "https://apihub.kma.go.kr/api/typ01/url/stn_pm10_inf.php?inf=kma&tm=" + formattedDate + "&help=0&authKey=kKsGKqfhRmurBiqn4ZZrLA";

        log.info("url : " + url);
        log.info("URL : " + URL);

        // Jsoup 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML소스 저장할 변수
        Document doc = null;

        // 사이트 접속
        doc = Jsoup.connect(url).get();

        Elements element = doc.select("body");

        // 적설 정보 가져오기
        Iterator<Element> dust = element.select("body").iterator();

        DustDTO pDTO = null;

        log.info("dust : " + dust);

        int idx = 0;

        // pre 태그에서 추출한 텍스트를 처리하고 DustDTO 객체에 값을 담아 리스트에 추가하는 로직 추가
        while (dust.hasNext()) {

            if (idx++ > 6) {
                break;
            }
            pDTO = new DustDTO();

            // pre 태그에서 추출한 텍스트 한 줄씩 읽어오기
            String line = CmmUtil.nvl(dust.next().text());

            log.info("line : " + line);

            log.info("ug/m의 위치 : " + line.indexOf("ug/"));
            log.info("#7777END의 위치 : " + line.indexOf("#7777END"));
            line = line.substring(134, 1178);

            log.info("substring 결과 : " + line);

            String[] lines = line.split("\\)"); // 난 한줄씩
            String[] dustInfoArray = line.split(" ");

            log.info("lines 줄 수 : " + lines.length);

            // 3번째 줄부터 출력하기
            for (int i = 0; i <= lines.length && (4 + 6 * i) < dustInfoArray.length; i++) {

                pDTO.setStnId(CmmUtil.nvl(dustInfoArray[3 + 6 * i]));
                pDTO.setMean(CmmUtil.nvl(dustInfoArray[4 + 6 * i]));

                log.info("-----------------------------------");
                log.info("stnId : " + pDTO.getStnId());
                log.info("avg : " + pDTO.getMean());

                dustMapper.updateDustInfo(pDTO);

            }
        }

        log.info(this.getClass().getName() + ".manageDustInfo End!");

    }

    @Scheduled(cron = "0 5 * * * *") // 매시 5분 0초마다 실행되게 스케줄러 설정
    public void updateDustInfo() throws Exception {

        log.info(this.getClass().getName() + ".updateDustInfo Start!");

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 날짜 및 시간 형식을 설정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        // 현재 시간을 원하는 형식으로 변환합니다.
        String formattedDate = dateFormat.format(currentDate);

        // 변환된 날짜와 시간을 사용하여 URL을 생성합니다.
        String url = "https://apihub.kma.go.kr/api/typ01/url/dst_pm10_hr.php?tm=" + formattedDate + "&help=0&org=%27kma%27&authKey=0a-KCqUxRzuvigqlMbc7rg";
        String URL = "https://apihub.kma.go.kr/api/typ01/url/stn_pm10_inf.php?inf=kma&tm=" + formattedDate + "&help=0&authKey=kKsGKqfhRmurBiqn4ZZrLA";

        log.info("url : " + url);
        log.info("URL : " + URL);

        // Jsoup 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML소스 저장할 변수
        Document doc = null;

        // 사이트 접속
        doc = Jsoup.connect(url).get();

        Elements element = doc.select("body");

        // 적설 정보 가져오기
        Iterator<Element> dust = element.select("body").iterator();

        DustDTO pDTO = null;

        log.info("dust : " + dust);

        int idx = 0;

        // pre 태그에서 추출한 텍스트를 처리하고 DustDTO 객체에 값을 담아 리스트에 추가하는 로직 추가
        while (dust.hasNext()) {

            if (idx++ > 6) {
                break;
            }
            pDTO = new DustDTO();

            // pre 태그에서 추출한 텍스트 한 줄씩 읽어오기
            String line = CmmUtil.nvl(dust.next().text());

            log.info("line : " + line);

            log.info("ug/m의 위치 : " + line.indexOf("ug/"));
            log.info("#7777END의 위치 : " + line.indexOf("#7777END"));
            line = line.substring(134, 1178);

            log.info("substring 결과 : " + line);

            String[] lines = line.split("\\)"); // 난 한줄씩
            String[] dustInfoArray = line.split(" ");

            log.info("lines 줄 수 : " + lines.length);

            // 3번째 줄부터 출력하기
            for (int i = 0; i <= lines.length && (4 + 6 * i) < dustInfoArray.length; i++) {

                pDTO.setStnId(CmmUtil.nvl(dustInfoArray[3 + 6 * i]));
                pDTO.setMean(CmmUtil.nvl(dustInfoArray[4 + 6 * i]));

                log.info("-----------------------------------");
                log.info("stnId : " + pDTO.getStnId());
                log.info("avg : " + pDTO.getMean());

                dustMapper.updateDustInfo(pDTO);

            }
        }

        log.info(this.getClass().getName() + ".updateDustInfo End!");

    }

}