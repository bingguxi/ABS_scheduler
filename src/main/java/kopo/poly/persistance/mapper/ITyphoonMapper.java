package kopo.poly.persistance.mapper;

import kopo.poly.dto.EarthquakeResultDTO;
import kopo.poly.dto.SnowDTO;
import kopo.poly.dto.TyphoonDTO;
import kopo.poly.dto.TyphoonLiveDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ITyphoonMapper {

    // 수집된 태풍 실시간 정보 DB에 등록
    int insertTyphoonLiveInfo(TyphoonLiveDTO pDTO) throws Exception;

    // DB에 저장된 태풍 실시간 정보 삭제하기
    int deleteTyphoonLiveInfo() throws Exception;

    // 수집된 태풍 실시간 정보 조회
    List<TyphoonLiveDTO> getTyphoonLiveInfo() throws Exception;

    // 수집된 태풍 과거 정보 DB에 등록
    int insertTyphoonInfo(TyphoonDTO pDTO) throws Exception;

    // DB에 저장된 태풍 과거 정보 삭제하기
//    int deleteTyphoonInfo() throws Exception;

    // 수집된 태풍 실시간 정보 조회
//    List<TyphoonDTO> getTyphoonInfo() throws Exception;
}
