package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DustDTO {

    public String stnId; // 국내 지점번호
    public String stnKo; // 지점명
    public String lon; // 경도
    public String lat; //  위도
    public String mean; // PM10 시간평균값

}
