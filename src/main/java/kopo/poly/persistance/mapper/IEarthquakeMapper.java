package kopo.poly.persistance.mapper;

import kopo.poly.dto.EarthquakeLiveDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IEarthquakeMapper {

    // 수집된 지진 실시간 정보 DB에 등록
    void insertEarthquakeLiveInfo(EarthquakeLiveDTO pDTO) throws Exception;

    // DB에 저장된 지진 실시간 정보 삭제하기
    void deleteEarthquakeLiveInfo() throws Exception;

    // 실시간 지진 정보 DB에 추가 삽입 전 존재여부 확인하기
    EarthquakeLiveDTO getExists(EarthquakeLiveDTO pDTO) throws Exception;
}
