package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.victorlamp.matrixiot.service.management.constant.ThingModelDataType;
import com.victorlamp.matrixiot.service.management.enums.ThingModelDataTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThingModelServiceParam extends ThingModelBase {
    @Serial
    private static final long serialVersionUID = 4806113181169085874L;

    private Integer order;

    @Valid
    @NotNull(message = "数据类型不能为空")
    private ThingModelDataTypeEnum dataType;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "dataType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DataSpec.Numeric.class, name = ThingModelDataType.NUMERIC),
            @JsonSubTypes.Type(value = DataSpec.Text.class, name = ThingModelDataType.TEXT),
            @JsonSubTypes.Type(value = DataSpec.Enum.class, name = ThingModelDataType.ENUM)})
    private DataSpec dataSpec;
}
