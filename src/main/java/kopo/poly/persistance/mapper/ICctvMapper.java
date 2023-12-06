package kopo.poly.persistance.mapper;

import kopo.poly.dto.CctvResultDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ICctvMapper {

    // CCTV URL 업데이트
    void updateCctvInfo(CctvResultDTO pDTO) throws Exception;

}
