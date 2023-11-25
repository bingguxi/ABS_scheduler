package kopo.poly.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import kopo.poly.dto.TyphoonDTO;
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
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TyphoonService {

    private final ITyphoonMapper typhoonMapper;

    @Value("${bhg.api.key}")
    private String apiKey;

    String apiURL = "https://apihub.kma.go.kr/api/typ02/openApi/SfcYearlyInfoService/getTyphoonList";

    /**
     * 태풍 실시간 API 크롤링해서 정보 가져오기
     * DB에 담아두었다가 새로운 요청이 오면 비우고 다시 DB에 담아서 서비스
     */
    
    public List<TyphoonLiveDTO> getTyphoonLiveInfo() throws Exception {

        log.info(this.getClass().getName() + ".getTyphoonLiveInfo Start!!");

        log.info("DB 삭제 시작");

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

        List<TyphoonLiveDTO> pList = new ArrayList<>();

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
                pDTO.setFt(CmmUtil.nvl(typhoonInfoArray[0 + 21 * i].replaceAll("= ", "")));
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

                pList.add(pDTO);
            }
        }
        List<TyphoonLiveDTO> rList = typhoonMapper.getTyphoonLiveInfo();

        log.info(this.getClass().getName() + ".getTyphoonLiveInfo End!");

        return rList;
    }

    /**
     * 태풍 과거 API 접근을 위한 URL 정보 생성 로직
     */
    
    public void setTyphoonUrl() throws Exception {
        log.info(this.getClass().getName() + ".setTyphoonUrl Start !");

        String pageNo = "1";
        String numOfRows = "100";
        String dataType = "XML";
        int year = 2015;

        int i = 0;  // 수정된 부분: 무한루프를 돌지 않도록 조건 수정
        while (i < 8) {
            log.info("현재 i : " + i);

            String apiParam = "?pageNo=" + pageNo + "&numOfRows=" + numOfRows + "&dataType=" + dataType + "&year=" + year + "&authKey=" + apiKey;
            log.info("apiParam : " + apiParam);

            // 수정된 부분: API 호출과 결과 처리를 별도의 메서드로 분리
            getTyphoonInfo(apiParam, year);

            i++;
            year++;
        }
        log.info(this.getClass().getName() + ".setTyphoonUrl End !");
    }

    /**
     * 위에서 받은 URL 정보를 가지고 태풍 과거 API 호출 후
     * XML 파싱 후 DB에 담는 로직
     */
    
    public void getTyphoonInfo(String apiParam, int year) throws Exception {

//        log.info(this.getClass().getName() + ".getTyphoonInfo Start !");
//
//        log.info("DB 삭제 시작");
//
//        typhoonMapper.deleteTyphoonInfo();

        HttpURLConnection urlConnection = null;

        try {
            // URL 객체를 생성하고 웹 주소를 넣습니다.
            URL url = new URL(apiURL + apiParam);
            log.info(url.toString());

            // HttpURLConnection 객체를 생성합니다.
            urlConnection = (HttpURLConnection) url.openConnection();
            // 요청 방식을 GET으로 설정합니다.
            urlConnection.setRequestMethod("GET");
            log.info("url 요청 전달 완료");

            // BufferedReader 객체를 생성하고 HttpURLConnection의 InputStream을 넣습니다.
            try (InputStream inputStream = urlConnection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                StringBuilder response = new StringBuilder();

                // 서버로부터 받은 데이터를 줄단위로 읽습니다.
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // API 응답 결과를 문자열로 변환
                String resultXml = response.toString();
                log.info("API 호출 결과 (시작태그는 <response>): " + resultXml);

                // XmlMapper를 사용하여 XML을 Java 객체로 변환
                XmlMapper xmlMapper = new XmlMapper();

                // TypeReference를 사용하여 제네릭 타입 지정
                TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
                Map<String, Object> resultMap = xmlMapper.readValue(resultXml, typeRef);

                // header와 body를 추출
                Map<String, Object> header = (Map<String, Object>) resultMap.get("header");
                Map<String, Object> body = (Map<String, Object>) resultMap.get("body");

                // header의 결과 코드 확인
                String resultCode = (String) header.get("resultCode");
                if ("00".equals(resultCode)) {
//                    Map<String, Object> responseBody = (Map<String, Object>) body.get("body");
                    Map<String, Object> items = (Map<String, Object>) body.get("items"); // body에서 items 추출
                    Map<String, Object> item = (Map<String, Object>) items.get("item"); // items에서 item 추출
                    Map<String, Object> ann = (Map<String, Object>) item.get("ann"); //  item에서 ann 추출
                    List<Map<String, Object>> infoList = (List<Map<String, Object>>) ann.get("info"); // ann에서 info 추출

                    // DTO로 변환하여 DB에 저장
                    List<TyphoonDTO> tList = new ArrayList<>();

                        for (Map<String, Object> info : infoList) {
                            TyphoonDTO tDTO = new TyphoonDTO();

                            tDTO.setTypSeq(String.valueOf(info.get("typ_seq")));
                            tDTO.setTypEn(String.valueOf(info.get("typ_en")));
                            tDTO.setYear(String.valueOf(year));
                            tDTO.setTmSt(String.valueOf(info.get("tm_st")));
                            tDTO.setTmEd(String.valueOf(info.get("tm_ed")));
                            tDTO.setTypPs(String.valueOf(info.get("typ_ps")));
                            tDTO.setTypWs(String.valueOf(info.get("typ_ws")));
                            tDTO.setTypName(String.valueOf(info.get("typ_name")));
                            tDTO.setEff(String.valueOf(info.get("eff")));

                            tList.add(tDTO);

                            log.info("typhoonDTO : " + tDTO);
                            log.info("typhoonList : " + tList);

                            typhoonMapper.insertTyphoonInfo(tDTO);

                            tDTO = null;
                        }

                    // DB에 저장 (주석 해제하여 활성화)
                    // typhoonMapper.insertTyphoonInfo(typhoonList);

                    log.info("매핑된 TyphoonDTO 리스트: " + tList);
                } else {
                    log.error("API 호출 결과 오류: " + resultCode);
                }
                // ... (이후 코드는 그대로 유지)
            } catch (Exception e) {
                log.error("Error in getTyphoonInfo: " + e.getMessage(), e);
            }
        } finally {
            // Ensure proper cleanup in case of exceptions or successful execution
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        log.info(this.getClass().getName() + ".getTyphoonInfo End !");
    }
}
