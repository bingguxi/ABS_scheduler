package kopo.poly.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EarthquakeResultDTO {

    /* 지진 과거 API 데이터를 담을 DTO ( XML ) */
    private String earthqaukeSeq;
    @JacksonXmlProperty(localName = "msgCode")
    private String msgCode; // 통보문종류명
    @JacksonXmlProperty(localName = "cntDiv")
    private String cntDiv; // 국내여부(국내:Y)
    @JacksonXmlProperty(localName = "arDiv")
    private String arDiv; // 지역구분(A:지역,S:해역,U:알수없음)
    @JacksonXmlProperty(localName = "eqArCdNm")
    private String eqArCdNm; // 지역상세
    @JacksonXmlProperty(localName = "eqPt")
    private String eqPt; // 발생위치
    @JacksonXmlProperty(localName = "nkDiv")
    private String nkDiv; // 북한여부 (북한:Y)
    @JacksonXmlProperty(localName = "tmIssue")
    private String tmIssue;
    @JacksonXmlProperty(localName = "eqDate")
    private String eqDate;
    @JacksonXmlProperty(localName = "magMl")
    private String magMl;
    @JacksonXmlProperty(localName = "magDiff")
    private String magDiff;
    @JacksonXmlProperty(localName = "eqDt")
    private String eqDt;
    @JacksonXmlProperty(localName = "eqLt")
    private String eqLt;
    @JacksonXmlProperty(localName = "eqLn")
    private String eqLn;
    @JacksonXmlProperty(localName = "majorAxis")
    private String majorAxis;
    @JacksonXmlProperty(localName = "minorAxis")
    private String minorAxis;
    @JacksonXmlProperty(localName = "depthDiff")
    private String depthDiff;
    @JacksonXmlProperty(localName = "jdLoc")
    private String jdLoc;
    @JacksonXmlProperty(localName = "jdLocA")
    private String jdLocA;
    @JacksonXmlProperty(localName = "ReFer")
    private String ReFer;

    @JacksonXmlProperty(localName = "infoList")
    private List<EarthquakeDTO> infoList;
}