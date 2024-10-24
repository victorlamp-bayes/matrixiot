package com.victorlamp.matrixiot.service.system.convert.dict;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.type.DictTypeCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.type.DictTypeRespVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.type.DictTypeSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.dict.vo.type.DictTypeUpdateReqVO;
import com.victorlamp.matrixiot.service.system.entity.dict.DictTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictTypeConvert {

    DictTypeConvert INSTANCE = Mappers.getMapper(DictTypeConvert.class);

    PageResult<DictTypeRespVO> convertPage(PageResult<DictTypeDO> bean);

    DictTypeRespVO convert(DictTypeDO bean);

    DictTypeDO convert(DictTypeCreateReqVO bean);

    DictTypeDO convert(DictTypeUpdateReqVO bean);

    List<DictTypeSimpleRespVO> convertList(List<DictTypeDO> list);

}
