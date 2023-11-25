package kopo.poly.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TyphoonDTO {

    /* 태풍 과거 API 데이터를 담을 DTO ( XML )*/

    @JacksonXmlProperty(localName = "typ_seq")
    private String typSeq; // 태풍번호
    @JacksonXmlProperty(localName = "typ_en")
    private String typEn; // 태풍명칭
    @JacksonXmlProperty(localName = "year")
    private String year; // 년도
    @JacksonXmlProperty(localName = "tm_st")
    private String tmSt; // 태풍발생일
    @JacksonXmlProperty(localName = "tm_ed")
    private String tmEd; // 태풍소멸일
    @JacksonXmlProperty(localName = "typ_ps")
    private String typPs; // 중심 최저기압
    @JacksonXmlProperty(localName = "typ_ws")
    private String typWs; // 최대 풍속
    @JacksonXmlProperty(localName = "typ_name")
    private String typName; // 태풍 한글명칭
    @JacksonXmlProperty(localName = "eff")
    private String eff; // eff
}
