package kopo.poly.persistance.mapper;

import kopo.poly.dto.EarthquakeLiveDTO;
import kopo.poly.dto.EarthquakeResultDTO;
import kopo.poly.dto.SnowDTO;
import kopo.poly.dto.TyphoonLiveDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IEarthquakeMapper {

    // 수집된 지진 실시간 정보 DB에 등록
    int insertEarthquakeLiveInfo(EarthquakeLiveDTO pDTO) throws Exception;

    // DB에 저장된 지진 실시간 정보 삭제하기
    int deleteEarthquakeLiveInfo() throws Exception;

    // 수집된 지진 실시간 정보 조회
    List<EarthquakeLiveDTO> getEarthquakeLiveInfo() throws Exception;

    // 수집된 지진 과거 정보 DB에 등록
    int insertEarthquakeInfo(EarthquakeResultDTO pDTO) throws Exception;

    // DB에 저장된 지진 과거 정보 삭제하기
//    int deleteEarthquakeInfo() throws Exception;

    // 수집된 지진 과거 정보 조회
//    List<EarthquakeResultDTO> getEarthquakeList() throws Exception;
}
