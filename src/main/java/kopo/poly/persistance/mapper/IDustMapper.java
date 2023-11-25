package kopo.poly.persistance.mapper;

import kopo.poly.dto.DustDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IDustMapper {

    // DB에 저장된 황사 정보 삭제
    void deleteDustInfo() throws Exception;

    // 황사 정보 수정
    void updateDustInfo(DustDTO pDTO) throws Exception;

}
