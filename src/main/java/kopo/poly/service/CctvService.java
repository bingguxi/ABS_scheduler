package kopo.poly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.CctvResultDTO;
import kopo.poly.persistance.mapper.ICctvMapper;
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
public class CctvService {

    private final ICctvMapper cctvMapper;
    @Value("${cctvResult.api.key}")
    private String apiKey;

    String url = "https://openapi.its.go.kr:9443/cctvInfo";

    @Scheduled(cron = "0 55 23 * * *") // 매일 23시 55분 0초에 실행되게 설정!
    public void updateCctvInfo() throws Exception {

        log.info(this.getClass().getName() + ".updateCctvInfo 시작!");

        String type = "ex";
        String cctvType = "1";
        String minX = "127.100000";
        String maxX = "128.890000";
        String minY = "34.100000";
        String maxY = "39.100000";
        String getType = "json";

        String apiParam = url + "?apiKey=" + apiKey + "&type=" + type + "&cctvType=" + cctvType + "&minX=" + minX + "&maxX=" + maxX + "&minY=" + minY + "&maxY=" + maxY + "&getType=" + getType;
        log.info("apiParam : " + apiParam);

        Map<String, String> headers = new HashMap<>();

        String json = NetworkUtil.get(apiParam, headers);

        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        // CCTV의 정보를 가지고 있는 data 키의 값 가져오기
        Map<String, Object> dMap = (Map<String, Object>) rMap.get("response");

        // 일별 날씨 조회(OpenAPI가 현재 날짜 기준으로 최대 7일까지 제공)
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dMap.get("data");

        for (Map<String, Object> dataMap : dataList) {
            CctvResultDTO pDTO = new CctvResultDTO();

            pDTO.setCctvName((String) dataMap.get("cctvname"));
            pDTO.setCctvUrl((String) dataMap.get("cctvurl"));

            cctvMapper.updateCctvInfo(pDTO);

        }

        log.info(this.getClass().getName() + ".updateCctvInfo 끝!");

    }

}
