package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EarthquakeLiveDTO {

    /* 지진 실시간 API 데이터를 담을 DTO ( json 파싱 )*/

    private String earthquakeLiveSeq; // 시퀀스
    private String cnt; // 참조번호
    private String fcTp; // 통보종류 ( 2 : 국외 / 3 : 국내 / 5 : 국내 ( 재통보 ) / 11 : 국내 조기경보 / 12 : 국외 조기경보 / 13 : 조기경보 정밀분석 )
    private String img; // 지도 이미지 ( 진앙 위치 이미지 URL - 통보종류 2,3번만 제공 )
    private String inT; // 진도 (시범서비스)
    private String lat; // 위도
    private String loc; // 진앙 위치
    private String lon; // 경도
    private String mt; // 규모
    private String rem; // 참고사항
    private String stnId; // 지점코드- 108(전국)으로 고정
    private String tmEqk; // 진앙시 ( 년월일시분초 ) - 지진 발생 시각
    private String tmFc; // 발표시각 ( 년월일시분 ) - 통보문 발표 시각
    private String tmSeq; // 발표 일련번호 ( 월별 )
    private String dep; // 깊이 - 발생깊이 ( km, 시범 운영 )
    private String existsYn;

}
