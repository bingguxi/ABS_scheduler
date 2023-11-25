package kopo.poly.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.MapApiDTO;
import kopo.poly.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MapService {

    
    public List<MapApiDTO> shelterMap() throws Exception {

        log.info(getClass().getName() + "지진 해일 대피장소 파싱 시작!!");

        String url = "http://223.130.129.189:9191/getTsunamiShelter1List/numOfRows=999&pageNo=1&type=json";

        String json = NetworkUtil.get(url);

        // 가져온 객체 JSON을 MAP형태로 변환
        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        List<Map<String, Object>> TsunamiShelter = (List<Map<String, Object>>) rMap.get("TsunamiShelter");

        // TsunamiShelter 객체의 두번째 객체니까 순번으로 1번에서 row로 된 것 가져오기
        List<Map<String, Object>>  row = (List<Map<String, Object>>) TsunamiShelter.get(1).get("row");

        // 변수에 담을 List
        List<MapApiDTO> resultList = new ArrayList<>();

        for (Map<String, Object> resultMap : row) {

            // 마지막 추출하기
            Double lat = (Double) resultMap.get("lat"); // 위도
            Double lon = (Double) resultMap.get("lon"); // 경도
            String shel_nm =(String) resultMap.get("shel_nm");

            // DTO에 담기
            MapApiDTO rDTO = new MapApiDTO();
            rDTO.setLat(lat.toString());
            rDTO.setLon(lon.toString());
            rDTO.setShel_nm(shel_nm);
            resultList.add(rDTO);

            rDTO = null;

        }


        log.info(getClass().getName() + "지진 해일 대피장소 파싱 끝!!");

        return resultList;
    }


}
