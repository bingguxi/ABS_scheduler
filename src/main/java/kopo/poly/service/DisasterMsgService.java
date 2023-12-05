package kopo.poly.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.DisasterMsgResultDTO;
import kopo.poly.persistance.mapper.IDisasterMsgMapper;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DisasterMsgService {

    private final IDisasterMsgMapper disasterMsgMapper;

    @Value("${data.api.key}")
    private String apiKey; // application.properties 또는 application.yml 파일에서 API 키 설정

    String apiURL = "https://apis.data.go.kr/1741000/DisasterMsg3/getDisasterMsg1List";

    @Scheduled(cron = "0 0/1 * * * *") // 매 1분 0초마다 실행되게 설정!
    public void manageDisasterMsg() throws Exception {

        log.info(this.getClass().getName() + ".manageDisasterMsg Start!!");

        log.info("DB 삭제 시작");

        disasterMsgMapper.deleteDisasterMsgInfo();

        String pageNo = "1";
        String numOfRows = "100";
        String type = "json";

        String apiParam = apiURL + "?serviceKey=" + apiKey + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows + "&type=" + type;
        log.info("apiParam : " + apiParam);

        Map<String, String> head = new HashMap<>();

        String json = NetworkUtil.get(apiParam, head);

        log.info("json : " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        // 수정: "DisasterMsg" 내의 "row"에 접근
        JsonNode dataList = rootNode.path("DisasterMsg");
        if (dataList.isArray()) {
            for (JsonNode dataNode : dataList) {
                // 수정: "row"에 접근
                JsonNode rowList = dataNode.path("row");
                if (rowList.isArray()) {
                    for (JsonNode rowNode : rowList) {
                        DisasterMsgResultDTO rDTO = new DisasterMsgResultDTO();

                        rDTO.setCreateDate(rowNode.path("create_date").asText());
                        rDTO.setLocationId(rowNode.path("location_id").asText());
                        rDTO.setLocationName(rowNode.path("location_name").asText());
                        rDTO.setMd101Sn(rowNode.path("md101_sn").asText());
                        rDTO.setMsg(rowNode.path("msg").asText());
                        rDTO.setSendPlatform(rowNode.path("send_platform").asText());

                        // '실종', '배회', '파업' 단어가 포함된 경우 데이터를 제외
                        if (!containsExcludedWords(rDTO.getMsg())) {
                            disasterMsgMapper.insertDisasterMsgInfo(rDTO);
                        }
                    }
                }
            }
        }

        log.info(this.getClass().getName() + ".manageDisasterMsg End!!");

    }

    // '실종', '배회', '파업' 단어가 포함되어 있는지 확인하는 메서드
    private boolean containsExcludedWords(String msg) {
        // 대소문자를 구분하지 않고 비교
        String lowerCaseMsg = msg.toLowerCase();
        return lowerCaseMsg.contains("실종") || lowerCaseMsg.contains("배회") || lowerCaseMsg.contains("파업") || lowerCaseMsg.contains("목격된") || lowerCaseMsg.contains("포획");
    }

}
