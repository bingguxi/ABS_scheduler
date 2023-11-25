package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CctvResultDTO {

    private String dataCount; // CCTV 개수
    private String roadSectionId; // 도로 구간 ID
    private String fileCreateTime; // 파일 생성 시간(YYYYMMDDHH24MISS)
    private String cctvUrl; //CCTV 영상 주소
    private String coordX; // 경도 좌표
    private String coordY; // 위도 좌표
    private String cctvName; // cctv 설치 장소명


}
