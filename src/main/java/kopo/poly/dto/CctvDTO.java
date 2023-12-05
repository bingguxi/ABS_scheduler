package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CctvDTO {

    private String type; // 도로 유형(ex: 고속도로 / its: 국도)
    private String cctvType; // CCTV 유형(1: 실시간 스트리밍(HLS) / 2: 동영상 파일 / 3: 정지 영상)
    private String minX; // 최소 경도 영역
    private String maxX; // 최대 경도 영역
    private String minY; // 최소 위도 영역
    private String maxY; // 최대 위도 영역
    private String getType; // 출력 결과 형식(xml, json / 기본: xml)

    // cctv 결과 값
    private List<CctvResultDTO> cctvList;
}
