package com.victorlamp.matrixiot.service.management.utils.excel;

import com.victorlamp.matrixiot.common.util.excel.CustomReadListener;
import com.victorlamp.matrixiot.service.management.controller.thing.vo.ThingImportExcelVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThingImportExcelVOListener extends CustomReadListener<ThingImportExcelVO> {


//    @Override
//    public void dataDeal() {
//        cachedDataList.forEach(item -> {
//            System.out.println(item);
//            ThingImportExcelVO excel = (ThingImportExcelVO) item;
//            System.out.println(excel.getName());
//        });
//    }

}
