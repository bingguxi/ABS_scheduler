package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TyphoonLiveDTO {

    /* 태풍 실시간 API 데이터를 담을 DTO ( php 크롤링 )*/

    private String typhoonLiveSeq; // 수집번호
    private String ft; // 0(분석), 1(예측)
    private String typ; // 태풍번호
    private String seq; // 발표번호
    private String typTm; // 태풍 분석시각 (UTC)
    private String lat; // 위도
    private String lon; // 경도
    private String dir; // 태풍진행방향 (16방위기호)
    private String sp; // 태풍진향속도 (km/h)
    private String ps; // 태풍중심기압 (hpa)
    private String ws; // 태풍최대풍속 (m/s)
    private String rad15; // 15m/s 반경 (km)
    private String rad25; // 25m/s 반경 (km)
    private String loc; // 위치
}
