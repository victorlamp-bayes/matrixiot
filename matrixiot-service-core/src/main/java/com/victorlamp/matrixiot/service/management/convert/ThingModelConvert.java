package com.victorlamp.matrixiot.service.management.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ThingModelConvert {

    ThingModelConvert INSTANCE = Mappers.getMapper(ThingModelConvert.class);
}
