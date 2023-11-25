package kopo.poly.persistance.mapper;

import kopo.poly.dto.SnowDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ISnowMapper {

    // 수집된 적설 정보 DB에 등록
    void insertSnowInfo(SnowDTO pDTO) throws Exception;

    // DB에 저장된 적설 정보 삭제하기
    void deleteSnowInfo() throws Exception;

    // 적설 정보 수정
    void updateSnowInfo(SnowDTO pDTO) throws Exception;
}
