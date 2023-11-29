package kopo.poly.persistance.mapper;

import kopo.poly.dto.TyphoonLiveDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ITyphoonMapper {

    // 수집된 태풍 실시간 정보 DB에 등록
    void insertTyphoonLiveInfo(TyphoonLiveDTO pDTO) throws Exception;

    // DB에 저장된 태풍 실시간 정보 삭제하기
    void deleteTyphoonLiveInfo() throws Exception;

    // 실시간 태풍 정보 DB에 추가 삽입 전 존재여부 확인하기
    TyphoonLiveDTO getExists(TyphoonLiveDTO pDTO) throws Exception;
}
