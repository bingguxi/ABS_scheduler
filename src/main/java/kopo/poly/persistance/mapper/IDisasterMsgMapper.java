package kopo.poly.persistance.mapper;

import kopo.poly.dto.DisasterMsgResultDTO;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface IDisasterMsgMapper {

    // 수집된 재난문자 정보 DB에 등록
    void insertDisasterMsgInfo(DisasterMsgResultDTO pDTO) throws Exception;

    // DB에 저장된 재난문자 정보 삭제하기
    void deleteDisasterMsgInfo() throws Exception;
   
}
