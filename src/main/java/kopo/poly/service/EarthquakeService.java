package kopo.poly.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.EarthquakeLiveDTO;
import kopo.poly.persistance.mapper.IEarthquakeMapper;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EarthquakeService {

    private final IEarthquakeMapper earthquakeMapper;

    // 지진 실시간 데이터 파싱을 위한 공공 데이터 포털 API KEY
    @Value("${data.api.key}")
    private String dataApiKey;

    String apiLiveURL = "http://apis.data.go.kr/1360000/EqkInfoService/getEqkMsg";

    /**
     * 지진 실시간 API JSON 파싱해서 정보 가져오기
     * DB에 담아두었다가 새로운 요청이 오면 비우고 다시 DB에 담아서 서비스 ( 일단 DB 저장 )
     * 당일 기준 3일까지의 데이터만 들어옴
     */
    @Scheduled(cron = "0 1 0 * * *") // 매일 오전 12시 1분 0초마다 실행되게 스케줄러 설정
    @Transactional
    void manageEarthquakeLiveInfo() throws Exception {
        log.info(this.getClass().getName() + ".manageEarthquakeLiveInfo Start!!");

        log.info("DB 삭제 시작");

        earthquakeMapper.deleteEarthquakeLiveInfo();

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // Calendar 객체를 생성하고 현재 날짜로 설정합니다.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // 3일 전의 날짜를 계산합니다. ( 3일전까지의 데이터까지 조회하기 위해 파라미터값으로 3일전 날짜를 생성 )
        calendar.add(Calendar.DAY_OF_MONTH, -3);

        // 형식을 설정하고 문자열로 변환합니다. ( 문자열로 변환하여 파라미터값으로 사용가능하게 변경 )
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedCurrentDate = dateFormat.format(currentDate);
        String threeDaysAgo = dateFormat.format(calendar.getTime());

        String pageNo = "1";
        String numOfRows = "100";
        String dataType = "json";

        String apiParam = apiLiveURL + "?serviceKey=" + dataApiKey + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo + "&dataType=" + dataType + "&fromTmFc=" + threeDaysAgo + "&toTmFc=" + formattedCurrentDate;
        log.info("apiParam : " + apiParam);

        Map<String, String> head = new HashMap<>();

        String json = NetworkUtil.get(apiParam, head);

        log.info("json : " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        // totalCount 값 가져오기 (asText() 메서드로 String으로 가져오기)
        String totalCount = rootNode.path("response").path("body").path("totalCount").asText();
        log.info("조회된 지진 실시간 데이터의 totalCount : " + totalCount);

        // items 노드 가져오기
        JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

        if (itemsNode.isArray()) {
            for (JsonNode node : itemsNode) {
                EarthquakeLiveDTO pDTO = new EarthquakeLiveDTO();

                // FIXME api json 키 값은 cctvname (카멜 케이스가 아닌데)
                // dto에서는 카멜식임
                pDTO.setCnt(node.path("cnt").asText());
                pDTO.setFcTp(node.path("fcTp").asText());
                pDTO.setImg(node.path("img").asText());
                pDTO.setInT(node.path("inT").asText());
                pDTO.setLat(node.path("lat").asText());
                pDTO.setLoc(node.path("loc").asText());
                pDTO.setLon(node.path("lon").asText());
                pDTO.setMt(node.path("mt").asText());
                pDTO.setRem(node.path("rem").asText());
                pDTO.setStnId(node.path("stnId").asText());
                pDTO.setTmEqk(node.path("tmEqk").asText());
                pDTO.setTmFc(node.path("tmFc").asText());
                pDTO.setTmSeq(node.path("tmSeq").asText());
                pDTO.setDep(node.path("dep").asText());

                earthquakeMapper.insertEarthquakeLiveInfo(pDTO);

            }
        }

        log.info(this.getClass().getName() + ".manageEarthquakeLiveInfo End!!");

    }

    @Scheduled(cron = "0 5 * * * *") // 매시 5분 0초마다 실행되게 스케줄러 설정
    void insertEarthquakeLiveInfo() throws Exception {
        log.info(this.getClass().getName() + ".insertEarthquakeLiveInfo Start!!");

        // 현재 시간을 가져옵니다.
        Date currentDate = new Date();

        // Calendar 객체를 생성하고 현재 날짜로 설정합니다.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // 3일 전의 날짜를 계산합니다. ( 3일전까지의 데이터까지 조회하기 위해 파라미터값으로 3일전 날짜를 생성 )
        calendar.add(Calendar.DAY_OF_MONTH, -3);

        // 형식을 설정하고 문자열로 변환합니다. ( 문자열로 변환하여 파라미터값으로 사용가능하게 변경 )
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedCurrentDate = dateFormat.format(currentDate);
        String threeDaysAgo = dateFormat.format(calendar.getTime());

        String pageNo = "1";
        String numOfRows = "100";
        String dataType = "json";

        String apiParam = apiLiveURL + "?serviceKey=" + dataApiKey + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo + "&dataType=" + dataType + "&fromTmFc=" + threeDaysAgo + "&toTmFc=" + formattedCurrentDate;
        log.info("apiParam : " + apiParam);

        Map<String, String> head = new HashMap<>();

        String json = NetworkUtil.get(apiParam, head);

        log.info("json : " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        // totalCount 값 가져오기 (asText() 메서드로 String으로 가져오기)
        String totalCount = rootNode.path("response").path("body").path("totalCount").asText();
        log.info("조회된 지진 실시간 데이터의 totalCount : " + totalCount);

        // items 노드 가져오기
        JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

        if (itemsNode.isArray()) {
            for (JsonNode node : itemsNode) {
                EarthquakeLiveDTO pDTO = new EarthquakeLiveDTO();

                pDTO.setTmEqk(node.path("tmEqk").asText());

                EarthquakeLiveDTO rDTO = earthquakeMapper.getExists(pDTO);

                if (rDTO.getExistsYn().equals("N")) {

                    pDTO.setCnt(node.path("cnt").asText());
                    pDTO.setFcTp(node.path("fcTp").asText());
                    pDTO.setImg(node.path("img").asText());
                    pDTO.setInT(node.path("inT").asText());
                    pDTO.setLat(node.path("lat").asText());
                    pDTO.setLoc(node.path("loc").asText());
                    pDTO.setLon(node.path("lon").asText());
                    pDTO.setMt(node.path("mt").asText());
                    pDTO.setRem(node.path("rem").asText());
                    pDTO.setStnId(node.path("stnId").asText());
                    pDTO.setTmFc(node.path("tmFc").asText());
                    pDTO.setTmSeq(node.path("tmSeq").asText());
                    pDTO.setDep(node.path("dep").asText());

                    earthquakeMapper.insertEarthquakeLiveInfo(pDTO);

                }
            }
        }

        log.info(this.getClass().getName() + ".insertEarthquakeLiveInfo End!!");

    }

}
