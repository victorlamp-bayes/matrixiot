package com.victorlamp.matrixiot.service.management.service.area;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.service.management.dao.AreaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("areaService")
@Slf4j
public class AreaServiceImpl implements AreaService {

    @Resource
    private AreaRepository areaRepository;

    @Override
    public JSONObject getAreaBound(int code) {
        Area area = areaRepository.getAreaByCode(code);
        if (ObjUtil.isEmpty(area)) {
            return null;
        }

        return area.getBound();
    }
}
