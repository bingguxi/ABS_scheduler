package kopo.poly.service;

import kopo.poly.dto.TyphoonLiveDTO;
import kopo.poly.persistance.mapper.ITyphoonMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TyphoonService {

    private final ITyphoonMapper typhoonMapper;

    @Value("${bhg.api.key}")
    private String apiKey;
    
    /**
     * 태풍 실시간 API 크롤링해서 정보 가져오기
     */
    @Scheduled(cron = "0 1 0 * * *") // 매일 오전 12시 1분 0초마다 실행되게 스케줄러 설정
    public void manageTyphoonLiveInfo() throws Exception {

        log.info(this.getClass().getName() + ".manageTyphoonLiveInfo Start!!");

        log.info("태풍 정보 DB 삭제 시작");

        typhoonMapper.deleteTyphoonLiveInfo();

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 날짜 및 시간 형식을 설정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        // 현재 시간을 원하는 형식으로 변환합니다.
        String formattedDate = dateFormat.format(currentDate);

        log.info("현재 시각 : " + formattedDate);

        // 변환된 날짜와 시간을 사용하여 URL을 생성합니다.
        String url = "https://apihub.kma.go.kr/api/typ01/url/typ_now.php?disp=1&help=0&mode=1&tm=" + formattedDate  + "&authKey=" + apiKey;

        // Jsoup 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML소스 저장할 변수
        Document doc = null;

        // 사이트 접속
        doc = Jsoup.connect(url).get();

        log.info("doc : " + doc);

        Elements element = doc.select("body");

        log.info("element : " + element);

        // 태풍 정보 가져오기
        Iterator<Element> typhoon = element.select("body").iterator();

        TyphoonLiveDTO pDTO = null;

        log.info("typhoon : " + typhoon);

        int idx = 0;

        // pre 태그에서 추출한 텍스트를 처리하고 TyphoonLiveDTO 객체에 값을 담아 리스트에 추가하는 로직 추가
        while (typhoon.hasNext()) {

            if (idx++ > 20) {
                break;
            }
            pDTO = new TyphoonLiveDTO();

            log.info("pDTO : " + pDTO);

            // pre 태그에서 추출한 텍스트 한 줄씩 읽어오기
            String line = CmmUtil.nvl(typhoon.next().text());

            log.info("line : " + line);

            log.info("ER25의 위치 : " + line.indexOf("ER25"));
            log.info("#7777END의 위치 : " + line.indexOf("#7777END"));

            line = line.replaceAll("#7777END", "");
            line = line.substring(211);

            log.info("substring 결과 : " + line);

            String[] lines = line.split("="); // 난 한줄씩
            String[] typhoonInfoArray = line.split(",");

            log.info("lines : " + lines.length);
            log.info("typhoonInfoArray : " + typhoonInfoArray.length);

            // 3번째 줄부터 출력하기
            for (int i = 0; i < lines.length && (19 + 21 * i) < typhoonInfoArray.length; i++) {

                pDTO.setFt(CmmUtil.nvl(typhoonInfoArray[21 * i].replaceAll("= ", "")));
                pDTO.setTyp(CmmUtil.nvl(typhoonInfoArray[2 + 21 * i]));
                pDTO.setSeq(CmmUtil.nvl(typhoonInfoArray[3 + 21 * i]));
                pDTO.setTypTm(CmmUtil.nvl(typhoonInfoArray[5 + 21 * i]));
                pDTO.setLat(CmmUtil.nvl(typhoonInfoArray[7 + 21 * i]));
                pDTO.setLon(CmmUtil.nvl(typhoonInfoArray[8 + 21 * i]));
                pDTO.setDir(CmmUtil.nvl(typhoonInfoArray[9 + 21 * i]));
                pDTO.setSp(CmmUtil.nvl(typhoonInfoArray[10 + 21 * i]));
                pDTO.setPs(CmmUtil.nvl(typhoonInfoArray[11 + 21 * i]));
                pDTO.setWs(CmmUtil.nvl(typhoonInfoArray[12 + 21 * i]));
                pDTO.setRad15(CmmUtil.nvl(typhoonInfoArray[13 + 21 * i]));
                pDTO.setRad25(CmmUtil.nvl(typhoonInfoArray[14 + 21 * i]));
                pDTO.setLoc(CmmUtil.nvl(typhoonInfoArray[18 + 21 * i]));

                log.info("-----------------------------------");
                log.info("FT : " + pDTO.getFt());
                log.info("TYP : " + pDTO.getTyp());
                log.info("SEQ : " + pDTO.getSeq());
                log.info("TYP_TM : " + pDTO.getTypTm());
                log.info("LAT : " + pDTO.getLat());
                log.info("LON : " + pDTO.getLon());
                log.info("DIR : " + pDTO.getDir());
                log.info("SP : " + pDTO.getSp());
                log.info("PS : " + pDTO.getPs());
                log.info("WS : " + pDTO.getWs());
                log.info("RAD15 : " + pDTO.getRad15());
                log.info("RAD25 : " + pDTO.getRad25());
                log.info("LOC : " + pDTO.getLoc());

                typhoonMapper.insertTyphoonLiveInfo(pDTO);

            }
        }

        log.info(this.getClass().getName() + ".manageTyphoonLiveInfo End!");

    }

    @Scheduled(cron = "0 1 0/6 * * *") // 매일 0시 1분에 시작해서 6시간 간격으로 스케줄링 설정
    public void insertTyphoonLiveInfo() throws Exception {

        log.info(this.getClass().getName() + ".insertTyphoonLiveInfo Start!");

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 날짜 및 시간 형식을 설정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        // 현재 시간을 원하는 형식으로 변환합니다.
        String formattedDate = dateFormat.format(currentDate);

        log.info("현재 시각 : " + formattedDate);

        // 변환된 날짜와 시간을 사용하여 URL을 생성합니다.
        String url = "https://apihub.kma.go.kr/api/typ01/url/typ_now.php?disp=1&help=0&mode=1&tm=" + formattedDate  + "&authKey=" + apiKey;

        // Jsoup 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML소스 저장할 변수
        Document doc = null;

        // 사이트 접속
        doc = Jsoup.connect(url).get();

        log.info("doc : " + doc);

        Elements element = doc.select("body");

        log.info("element : " + element);

        // 태풍 정보 가져오기
        Iterator<Element> typhoon = element.select("body").iterator();

        TyphoonLiveDTO pDTO = null;

        log.info("typhoon : " + typhoon);

        int idx = 0;

        // pre 태그에서 추출한 텍스트를 처리하고 TyphoonLiveDTO 객체에 값을 담아 리스트에 추가하는 로직 추가
        while (typhoon.hasNext()) {

            if (idx++ > 20) {
                break;
            }
            pDTO = new TyphoonLiveDTO();

            log.info("pDTO : " + pDTO);

            // pre 태그에서 추출한 텍스트 한 줄씩 읽어오기
            String line = CmmUtil.nvl(typhoon.next().text());

            log.info("line : " + line);

            log.info("ER25의 위치 : " + line.indexOf("ER25"));
            log.info("#7777END의 위치 : " + line.indexOf("#7777END"));

            line = line.replaceAll("#7777END", "");
            line = line.substring(211);

            log.info("substring 결과 : " + line);

            String[] lines = line.split("="); // 난 한줄씩
            String[] typhoonInfoArray = line.split(",");

            log.info("lines : " + lines.length);
            log.info("typhoonInfoArray : " + typhoonInfoArray.length);

            // 3번째 줄부터 출력하기
            for (int i = 0; i < lines.length && (19 + 21 * i) < typhoonInfoArray.length; i++) {

                assert pDTO != null;
                pDTO.setTyp(CmmUtil.nvl(typhoonInfoArray[2 + 21 * i]));
                pDTO.setFtTm(CmmUtil.nvl(typhoonInfoArray[6 + 21 * i]));

                TyphoonLiveDTO rDTO = typhoonMapper.getExists(pDTO);

                if (rDTO.getExistsYn().equals("N")) {

                    pDTO.setFt(CmmUtil.nvl(typhoonInfoArray[21 * i].replaceAll("= ", "")));
                    pDTO.setSeq(CmmUtil.nvl(typhoonInfoArray[3 + 21 * i]));
                    pDTO.setTypTm(CmmUtil.nvl(typhoonInfoArray[5 + 21 * i]));
                    pDTO.setLat(CmmUtil.nvl(typhoonInfoArray[7 + 21 * i]));
                    pDTO.setLon(CmmUtil.nvl(typhoonInfoArray[8 + 21 * i]));
                    pDTO.setDir(CmmUtil.nvl(typhoonInfoArray[9 + 21 * i]));
                    pDTO.setSp(CmmUtil.nvl(typhoonInfoArray[10 + 21 * i]));
                    pDTO.setPs(CmmUtil.nvl(typhoonInfoArray[11 + 21 * i]));
                    pDTO.setWs(CmmUtil.nvl(typhoonInfoArray[12 + 21 * i]));
                    pDTO.setRad15(CmmUtil.nvl(typhoonInfoArray[13 + 21 * i]));
                    pDTO.setRad25(CmmUtil.nvl(typhoonInfoArray[14 + 21 * i]));
                    pDTO.setLoc(CmmUtil.nvl(typhoonInfoArray[18 + 21 * i]));

                    log.info("-----------------------------------");
                    log.info("FT : " + pDTO.getFt());
                    log.info("TYP : " + pDTO.getTyp());
                    log.info("SEQ : " + pDTO.getSeq());
                    log.info("TYP_TM : " + pDTO.getTypTm());
                    log.info("LAT : " + pDTO.getLat());
                    log.info("LON : " + pDTO.getLon());
                    log.info("DIR : " + pDTO.getDir());
                    log.info("SP : " + pDTO.getSp());
                    log.info("PS : " + pDTO.getPs());
                    log.info("WS : " + pDTO.getWs());
                    log.info("RAD15 : " + pDTO.getRad15());
                    log.info("RAD25 : " + pDTO.getRad25());
                    log.info("LOC : " + pDTO.getLoc());

                    typhoonMapper.insertTyphoonLiveInfo(pDTO);

                    pDTO = null;
                }
            }
        }

        log.info(this.getClass().getName() + ".insertTyphoonLiveInfo End!");

    }
    
}
