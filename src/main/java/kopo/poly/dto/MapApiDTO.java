package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapApiDTO {

    private String lon; // 경도
    private String lat; // 위도

    private String shel_nm; // 대피장소명
}
