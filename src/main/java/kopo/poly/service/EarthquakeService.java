package kopo.poly.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import kopo.poly.dto.EarthquakeLiveDTO;
import kopo.poly.dto.EarthquakeResultDTO;
import kopo.poly.persistance.mapper.IEarthquakeMapper;
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
public class EarthquakeService {

    private final IEarthquakeMapper earthquakeMapper;

    @Value("${bhg.api.key}")
    private String apiKey;

    String apiURL = "https://apihub.kma.go.kr/api/typ09/url/eqk/urlNewNotiEqk.do";

    /**
     * 지진 실시간 API 크롤링해서 정보 가져오기
     * DB에 담아두었다가 새로운 요청이 오면 비우고 다시 DB에 담아서 서비스
     */
    
    public List<EarthquakeLiveDTO> getEarthquakeLiveInfo() throws Exception {

        log.info(this.getClass().getName() + ".getEarthquakeLiveInfo Start!!");

        log.info("DB 삭제 시작");

        earthquakeMapper.deleteEarthquakeLiveInfo();


        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 날짜 및 시간 형식을 설정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        // 현재 시간을 원하는 형식으로 변환합니다.
        String formattedDate = dateFormat.format(currentDate);

        log.info("현재 시각 : " + formattedDate);

        // 변환된 날짜와 시간을 사용하여 URL을 생성합니다.
        String url = "https://apihub.kma.go.kr/api/typ01/url/eqk_now.php?tm=" + formattedDate /*"201307231215"*/ + "&disp=1&help=0&authKey=" + apiKey;

        // Jsoup 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML소스 저장할 변수
        Document doc = null;

        // 사이트 접속
        doc = Jsoup.connect(url).get();

        log.info("doc : " + doc);

        Elements element = doc.select("body");

        log.info("element : " + element);

        // 지진 정보 가져오기
        Iterator<Element> earthquake = element.select("body").iterator();

        EarthquakeLiveDTO pDTO = null;


        log.info("earthquake : " + earthquake);

        List<EarthquakeLiveDTO> pList = new ArrayList<>();

        int idx = 0;

        // pre 태그에서 추출한 텍스트를 처리하고 EarthquakeLiveDTO 객체에 값을 담아 리스트에 추가하는 로직 추가
        while (earthquake.hasNext()) {

            if (idx++ > 20) {
                break;
            }
            pDTO = new EarthquakeLiveDTO();

            log.info("pDTO : " + pDTO);

            // pre 태그에서 추출한 텍스트 한 줄씩 읽어오기
            String line = CmmUtil.nvl(earthquake.next().text());

            log.info("line : " + line);

            log.info("=의 위치 : " + line.indexOf("="));
            log.info("\n의 위치 : " + line.indexOf("\\\n,"));

//            line = line.replaceAll("#7777END", "");
            line = line.substring(63);

            log.info("substring 결과 : " + line);

            String[] lines = line.split("="); // 난 한줄씩
            String[] earthquakeInfoArray = line.split(",");


            log.info("lines : " + lines.length);
            log.info("earthquakeInfoArray : " + earthquakeInfoArray.length);


            // 3번째 줄부터 출력하기
            for (int i = 0; i < lines.length && (11 + 11 * i) < earthquakeInfoArray.length; i++) {
                pDTO.setTp(CmmUtil.nvl(earthquakeInfoArray[0 + 11 * i].replaceAll("= ", "")));
                pDTO.setTmFc(CmmUtil.nvl(earthquakeInfoArray[1 + 11 * i]));
                pDTO.setSeq(CmmUtil.nvl(earthquakeInfoArray[2 + 11 * i]));
                pDTO.setTmEqkMsc(CmmUtil.nvl(earthquakeInfoArray[3 + 11 * i]));
                pDTO.setMt(CmmUtil.nvl(earthquakeInfoArray[4 + 11 * i]));
                pDTO.setLat(CmmUtil.nvl(earthquakeInfoArray[5 + 11 * i]));
                pDTO.setLon(CmmUtil.nvl(earthquakeInfoArray[6 + 11 * i]));
                pDTO.setLoc(CmmUtil.nvl(earthquakeInfoArray[7 + 11 * i]));
                pDTO.setScale(CmmUtil.nvl(earthquakeInfoArray[8 + 11 * i]));
                pDTO.setRem(CmmUtil.nvl(earthquakeInfoArray[9 + 11 * i].replaceAll("\\\\n", "")));
                pDTO.setCor(CmmUtil.nvl(earthquakeInfoArray[10 + 11 * i]));

                log.info("-----------------------------------");
                log.info("TP : " + pDTO.getTp());
                log.info("TM_FC : " + pDTO.getTmFc());
                log.info("SEQ : " + pDTO.getSeq());
                log.info("TmEqkMsk : " + pDTO.getTmEqkMsc());
                log.info("MT : " + pDTO.getMt());
                log.info("LAT : " + pDTO.getLat());
                log.info("LON : " + pDTO.getLon());
                log.info("LOC : " + pDTO.getLoc());
                log.info("SCALE : " + pDTO.getScale());
                log.info("REM : " + pDTO.getRem());
                log.info("COR : " + pDTO.getCor());


                earthquakeMapper.insertEarthquakeLiveInfo(pDTO);

                pList.add(pDTO);
            }
        }
        List<EarthquakeLiveDTO> rList = earthquakeMapper.getEarthquakeLiveInfo();

        log.info(this.getClass().getName() + ".getEarthquakeLiveInfo End!");

        return rList;
    }

    /**
     * 지진 과거 API 접근을 위한 URL 정보 생성 로직
     */
    
    public void setEarthquakeUrl() throws Exception {

        log.info(this.getClass().getName() + ".setEarthquakeUrl Start !");

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // 날짜 및 시간 형식을 설정합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

        // 현재 시간을 원하는 형식으로 변환합니다.
        String formattedDate = dateFormat.format(currentDate);

        log.info("현재 시각 : " + formattedDate);

        // 과거 지진데이터가 있는 기준일로부터 현재 시각까지의 데이터를 수집하기 위한 URL 정보 생성
        String frDate = "20180101";
        String laDate = formattedDate;
        String cntDiv = "Y";
        String orderTy = "xml";

        int i = 0;  // 수정된 부분: 무한루프를 돌지 않도록 조건 수정
        while (i < 1) {
            log.info("현재 i : " + i);

            String apiParam = "?frDate=" + frDate + "&laDate=" + laDate + "&cntDiv=" + cntDiv + "&orderTy=" + orderTy + "&authKey=" + apiKey;
            log.info("apiParam : " + apiParam);

            // 수정된 부분: API 호출과 결과 처리를 별도의 메서드로 분리
            getEarthquakeInfo(apiParam);

            i++;
        }
        log.info(this.getClass().getName() + ".setEarthquakeUrl End !");
    }

    /**
     * 위에서 받은 URL 정보를 가지고 지진 과거 API 호출 후
     * XML 파싱 후 DB에 담는 로직
     */
    
    public void getEarthquakeInfo(String apiParam) {
        try {
            // URL 객체를 생성하고 웹 주소를 넣습니다.
            URL url = new URL(apiURL + apiParam);
            log.info(url.toString());

            // HttpURLConnection 객체를 생성합니다.
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
                log.info("API 호출 결과 (시작태그는 <alert>): " + resultXml);

                // XmlMapper를 사용하여 XML을 Java 객체로 변환
                XmlMapper xmlMapper = new XmlMapper();

//                List <LinkedHashMap<String, LinkedHashMap<String, String>>> resultData = xmlMapper.readValue(resultXml, typeRef);

                // TypeReference를 사용하여 제네릭 타입 지정
                TypeReference<List<Object>> typeRef = new TypeReference<List<Object>>() {};
                List<Object> resultData = xmlMapper.readValue(resultXml, typeRef);
                log.info("rd : " + resultData);

                List<Map<String, String>> dataList = new ArrayList<>();

                // 결과 데이터를 가공하여 필요한 정보 추출
                for (Object item : resultData) {
                    if (item instanceof LinkedHashMap) {
                        LinkedHashMap<String, Object> itemMap = (LinkedHashMap<String, Object>) item;

                        for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                            if ("info".equals(entry.getKey()) && entry.getValue() instanceof ArrayList) {
                                ArrayList<LinkedHashMap<String, String>> items = (ArrayList<LinkedHashMap<String, String>>) entry.getValue();

                                for (LinkedHashMap<String, String> infoMap : items) {
                                    // 이제 infoMap에서 필요한 정보를 추출하여 Map에 담을 수 있습니다.
                                    Map<String, String> dataMap = new HashMap<>(infoMap);
                                    dataList.add(dataMap);
                                }
                            }
                        }
                    }
                }

                log.info("dataList : " + dataList);

                log.info("xmlMapper의 readValue 작업 완료");

                log.info("resultData : " + resultData);
                log.info("생성된 DTO의 개수 : " + resultData.size());

                List<EarthquakeResultDTO> erList = new ArrayList<>();

                // DTO로 변환하여 DB에 저장
                for (Map<String, String> resultDTO : dataList) {

                    EarthquakeResultDTO erDTO = new EarthquakeResultDTO();

                    log.info("resultDTO : " + resultDTO);

                    erDTO.setMsgCode(resultDTO.get("msgCode"));
                    erDTO.setCntDiv(resultDTO.get("cntDiv"));
                    erDTO.setArDiv(resultDTO.get("arDiv"));
                    erDTO.setEqArCdNm(resultDTO.get("eqArCdNm"));
                    erDTO.setEqPt(resultDTO.get("eqPt"));
                    erDTO.setNkDiv(resultDTO.get("nkDiv"));
                    erDTO.setTmIssue(resultDTO.get("tmIssue"));
                    erDTO.setEqDate(resultDTO.get("eqDate"));
                    erDTO.setMagMl(resultDTO.get("magMl"));
                    erDTO.setMagDiff(resultDTO.get("magDiff"));
                    erDTO.setEqDt(resultDTO.get("eqDt"));
                    erDTO.setEqLt(resultDTO.get("eqLt"));
                    erDTO.setEqLn(resultDTO.get("eqLn"));
                    erDTO.setMajorAxis(resultDTO.get("majorAxis"));
                    erDTO.setMinorAxis(resultDTO.get("minorAxis"));
                    erDTO.setDepthDiff(resultDTO.get("depthDiff"));
                    erDTO.setJdLoc(resultDTO.get("jdLoc"));
                    erDTO.setJdLocA(resultDTO.get("jdLocA"));
                    erDTO.setReFer(resultDTO.get("ReFer"));

                    erList.add(erDTO);

                    log.info("erDTO : " + erDTO);
                    log.info("erList : " + erList);

                    earthquakeMapper.insertEarthquakeInfo(erDTO);

                    erDTO = null;
                }

                log.info("매핑된 EarthquakeResultDTO 리스트 : " + erList);
            }
            // 예외 발생시 스택트레이스를 출력합니다.
        } catch (Exception e) {
            log.error("Error in processEarthquakeData: " + e.getMessage(), e);
        }
    }
}
