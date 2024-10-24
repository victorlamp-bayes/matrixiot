package com.victorlamp.matrixiot.service.management.utils.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.victorlamp.matrixiot.service.management.entity.thingmodel.*;

import java.util.ArrayList;
import java.util.List;

import static com.victorlamp.matrixiot.service.management.constant.ThingModelDataType.*;

public class ThingModelConverter implements Converter<ThingModel> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return ThingModel.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public ThingModel convertToJavaData(ReadConverterContext<?> context) {
        JSONObject jsonObject = JSON.parseObject(context.getReadCellData().getStringValue());

        ThingModel thingModel = JSON.parseObject(context.getReadCellData().getStringValue(), ThingModel.class);

        if (CollUtil.isNotEmpty(thingModel.getProperties())) {
            JSONArray propertyJsonArray = jsonObject.getJSONArray("properties");
            //属性的转换
            List<ThingModelProperty> propertyList = new ArrayList<>();
            for (ThingModelProperty property : thingModel.getProperties()) {
                // 根据identifier找到对应JSON对象
                JSONObject propertyJsonObj = findFirstByIdentifier(property.getIdentifier(), propertyJsonArray);
                // 转换DataSpec
                DataSpec dataSpec = convertToDataSpec(propertyJsonObj.getString("dataType").toUpperCase(), propertyJsonObj.getString("dataSpec"));
                property.setDataSpec(dataSpec);

                propertyList.add(property);
            }
            thingModel.setProperties(propertyList);
        }

        if (CollUtil.isNotEmpty(thingModel.getServices())) {
            List<ThingModelService> serviceList = new ArrayList<>();

            JSONArray serviceJsonArray = jsonObject.getJSONArray("services");
            for (ThingModelService service : thingModel.getServices()) {
                // 根据identifier找到对应JSON对象
                JSONObject serviceJsonObj = findFirstByIdentifier(service.getIdentifier(), serviceJsonArray);

                // 输入参数
                List<ThingModelServiceParam> inputParams = JSON.parseArray(serviceJsonObj.getString("inputParams"), ThingModelServiceParam.class);
                JSONArray inputParamsArray = serviceJsonObj.getJSONArray("inputParams");
                for (ThingModelServiceParam param : inputParams) {
                    JSONObject paramJsonObj = findFirstByIdentifier(param.getIdentifier(), inputParamsArray);
                    DataSpec dataSpec = convertToDataSpec(paramJsonObj.getString("dataType"), paramJsonObj.getString("dataSpec"));
                    param.setDataSpec(dataSpec);
                }
                service.setInputParams(inputParams);

                // 输出参数
                List<ThingModelServiceParam> outputParams = JSON.parseArray(serviceJsonObj.getString("outputParams"), ThingModelServiceParam.class);
                JSONArray outputParamsArray = serviceJsonObj.getJSONArray("outputParams");
                for (ThingModelServiceParam param : outputParams) {
                    JSONObject paramJsonObj = findFirstByIdentifier(param.getIdentifier(), outputParamsArray);
                    DataSpec dataSpec = convertToDataSpec(paramJsonObj.getString("dataType"), paramJsonObj.getString("dataSpec"));
                    param.setDataSpec(dataSpec);
                }
                service.setOutputParams(outputParams);

                serviceList.add(service);
            }
            thingModel.setServices(serviceList);
        }

        return thingModel;
    }

    private DataSpec convertToDataSpec(String dataType, String dataSpecStr) {
        switch (dataType) {
            case TEXT -> {
                return JSON.parseObject(dataSpecStr, DataSpec.Text.class);
            }
            case ENUM -> {
                DataSpec.Enum enumDataSpec = JSON.parseObject(dataSpecStr, DataSpec.Enum.class);

                JSONObject dataSpecJson = JSON.parseObject(dataSpecStr);
                String enumValuesStr = dataSpecJson.getString("enumValues");
                List<DataSpec.Enum.EnumValue> enumValues = JSON.parseArray(enumValuesStr, DataSpec.Enum.EnumValue.class);

                enumDataSpec.setEnumValues(enumValues);

                return enumDataSpec;
            }
            case NUMERIC -> {
                return JSON.parseObject(dataSpecStr, DataSpec.Numeric.class);
            }
            default -> {
                return null;
            }
        }
    }

    private JSONObject findFirstByIdentifier(String identifier, JSONArray jsonArray) {
        List<JSONObject> list = jsonArray.toList(JSONObject.class).stream().filter(item -> StrUtil.equals(identifier, item.getString("identifier"))).toList();
        return list.get(0);
    }
}
