package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DisasterMsgResultDTO {

    /**
     * 재난문자 API 실제 파싱값이 저장되는 DTO
     */
    private String createDate; // 생성일시
    private String locationId; // 수신지역 코드
    private String locationName; // 수신지역 이름
    private String md101Sn; // 일련번호
    private String msg; // 내용
    private String sendPlatform; // 발신처

}
