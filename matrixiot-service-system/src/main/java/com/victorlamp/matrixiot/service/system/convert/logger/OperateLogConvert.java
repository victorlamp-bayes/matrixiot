package com.victorlamp.matrixiot.service.system.convert.logger;

import com.victorlamp.matrixiot.common.util.collection.CollectionUtils;
import com.victorlamp.matrixiot.common.util.collection.MapUtils;
import com.victorlamp.matrixiot.common.util.object.BeanUtils;
import com.victorlamp.matrixiot.service.system.controller.logger.vo.operatelog.OperateLogRespVO;
import com.victorlamp.matrixiot.service.system.entity.logger.OperateLogDO;
import com.victorlamp.matrixiot.service.system.entity.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperateLogConvert {

    OperateLogConvert INSTANCE = Mappers.getMapper(OperateLogConvert.class);

    default List<OperateLogRespVO> convertList(List<OperateLogDO> list, Map<Long, AdminUserDO> userMap) {
        return CollectionUtils.convertList(list, log -> {
            OperateLogRespVO logVO = BeanUtils.toBean(log, OperateLogRespVO.class);
            MapUtils.findAndThen(userMap, log.getUserId(), user -> logVO.setUserNickname(user.getNickname()));
            return logVO;
        });
    }

}
