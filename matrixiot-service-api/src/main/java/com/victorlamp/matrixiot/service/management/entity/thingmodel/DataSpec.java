package com.victorlamp.matrixiot.service.management.entity.thingmodel;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.victorlamp.matrixiot.service.management.constant.ThingModelDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class DataSpec implements Serializable {
    @Serial
    private static final long serialVersionUID = -1548270658876715601L;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(ThingModelDataType.NUMERIC)
    public static class Numeric extends DataSpec implements Serializable {
        @Serial
        private static final long serialVersionUID = 5935787504224800870L;
        private Number defaultValue;
        private Number min;
        private Number max;
        private Number step;
        private String unit;
        private String unitName;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(ThingModelDataType.TEXT)
    public static class Text extends DataSpec implements Serializable {
        @Serial
        private static final long serialVersionUID = -3118276668244592358L;
        private String defaultValue;
        private Integer length;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @JsonTypeName(ThingModelDataType.ENUM)
    public static class Enum extends DataSpec implements Serializable {
        @Serial
        private static final long serialVersionUID = -5080240581624398088L;

        private Number defaultValue;
        private List<EnumValue> enumValues;

        @Data
        public static class EnumValue implements Serializable {
            @Serial
            private static final long serialVersionUID = -7615586624174669569L;
            private String name;
            private Number value;
        }
    }
}

