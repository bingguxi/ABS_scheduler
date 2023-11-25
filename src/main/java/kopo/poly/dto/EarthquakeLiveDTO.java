package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EarthquakeLiveDTO {

    /* 지진 실시간 API 데이터를 담을 DTO ( php 크롤링 )*/

    private String earthquakeLiveSeq; // 시퀀스
    private String tp; // 3(국내지진통보), 2(국외지진정보)
    private String tmFc; // 발표시간
    private String seq; //발표일련번호
    private String tmEqkMsc; // 진앙시(년월일시분초)
    private String mt; // 규모
    private String lat; // 진앙 위도 (deg.)
    private String lon; // 진앙 경도 (deg.)
    private String loc; // 진앙 위치
    private String scale; // 진도
    private String rem; // 참고사항
    private String cor; // 수정사항
}
