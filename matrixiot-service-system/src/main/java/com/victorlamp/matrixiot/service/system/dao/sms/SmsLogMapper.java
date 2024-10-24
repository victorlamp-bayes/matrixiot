package com.victorlamp.matrixiot.service.system.dao.sms;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.framework.mybatis.core.mapper.BaseMapperX;
import com.victorlamp.matrixiot.service.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.victorlamp.matrixiot.service.system.controller.sms.vo.log.SmsLogPageReqVO;
import com.victorlamp.matrixiot.service.system.entity.sms.SmsLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsLogMapper extends BaseMapperX<SmsLogDO> {

    default PageResult<SmsLogDO> selectPage(SmsLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SmsLogDO>()
                .eqIfPresent(SmsLogDO::getChannelId, reqVO.getChannelId())
                .eqIfPresent(SmsLogDO::getTemplateId, reqVO.getTemplateId())
                .likeIfPresent(SmsLogDO::getMobile, reqVO.getMobile())
                .eqIfPresent(SmsLogDO::getSendStatus, reqVO.getSendStatus())
                .betweenIfPresent(SmsLogDO::getSendTime, reqVO.getSendTime())
                .eqIfPresent(SmsLogDO::getReceiveStatus, reqVO.getReceiveStatus())
                .betweenIfPresent(SmsLogDO::getReceiveTime, reqVO.getReceiveTime())
                .orderByDesc(SmsLogDO::getId));
    }

}
