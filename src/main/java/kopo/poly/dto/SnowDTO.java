package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnowDTO {

    public String stnId; // 국내 지점번호
    public String stnKo; // 지점명
    public String lon; // 경도
    public String lat; //  위도

    public String sd; // 적설값

    public String dt; // 날짜

}
