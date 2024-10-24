package com.victorlamp.matrixiot.service.system.convert.dept;

import com.victorlamp.matrixiot.service.system.controller.dept.vo.dept.DeptCreateReqVO;
import com.victorlamp.matrixiot.service.system.controller.dept.vo.dept.DeptRespVO;
import com.victorlamp.matrixiot.service.system.controller.dept.vo.dept.DeptSimpleRespVO;
import com.victorlamp.matrixiot.service.system.controller.dept.vo.dept.DeptUpdateReqVO;
import com.victorlamp.matrixiot.service.system.dto.DeptRespDTO;
import com.victorlamp.matrixiot.service.system.entity.dept.DeptDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    List<DeptRespVO> convertList(List<DeptDO> list);

    List<DeptSimpleRespVO> convertList02(List<DeptDO> list);

    DeptRespVO convert(DeptDO bean);

    DeptDO convert(DeptCreateReqVO bean);

    DeptDO convert(DeptUpdateReqVO bean);

    List<DeptRespDTO> convertList03(List<DeptDO> list);

    DeptRespDTO convert03(DeptDO bean);

}
