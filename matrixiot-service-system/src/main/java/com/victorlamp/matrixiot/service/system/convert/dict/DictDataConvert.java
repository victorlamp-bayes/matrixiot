package com.victorlamp.matrixiot.service.system.convert.dict;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.api.dict.dto.DictDataRespDTO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.AppDictDataRespVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.data.DictDataCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.data.DictDataRespVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.data.DictDataSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.data.DictDataUpdateReqVO;
import com.victorlamp.matrixiot.service.system.entity.dict.DictDataDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictDataConvert {

    DictDataConvert INSTANCE = Mappers.getMapper(DictDataConvert.class);

    List<DictDataSimpleRespVO> convertList(List<DictDataDO> list);

    DictDataRespVO convert(DictDataDO bean);

    PageResult<DictDataRespVO> convertPage(PageResult<DictDataDO> page);

    DictDataDO convert(DictDataUpdateReqVO bean);

    DictDataDO convert(DictDataCreateReqVO bean);

    DictDataRespDTO convert02(DictDataDO bean);

    List<AppDictDataRespVO> convertList03(List<DictDataDO> list);

}
