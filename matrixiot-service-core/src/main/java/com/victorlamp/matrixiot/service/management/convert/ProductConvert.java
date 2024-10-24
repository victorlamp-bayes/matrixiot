package com.victorlamp.matrixiot.service.management.convert;

import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductCreateReqVO;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductImportExcelVO;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductSimpleRespVO;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductUpdateStatusReqVO;
import com.victorlamp.matrixiot.service.management.dto.product.ProductCreateReqDTO;
import com.victorlamp.matrixiot.service.management.dto.product.ProductImportReqDTO;
import com.victorlamp.matrixiot.service.management.dto.product.ProductUpdateReqDTO;
import com.victorlamp.matrixiot.service.management.dto.product.ProductUpdateStatusReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductConvert {

    ProductConvert INSTANCE = Mappers.getMapper(ProductConvert.class);

    /*** VO to DTO ***/
    ProductCreateReqDTO toDTO(ProductCreateReqVO vo);

    ProductUpdateStatusReqDTO toDTO(ProductUpdateStatusReqVO vo);

    Product toEntity(ProductCreateReqDTO dto);

    Product toEntity(ProductUpdateReqDTO dto);

    ProductCreateReqDTO importToCreateDTO(ProductImportReqDTO importReqDTO);

    ProductUpdateReqDTO importToUpdateDTO(ProductImportReqDTO importReqDTO);

    ProductSimpleRespVO toSimple(Product product);

    List<ProductSimpleRespVO> toSimpleList(List<Product> list);

    ProductImportReqDTO importVOToReqDTO(ProductImportExcelVO excelVO);

    List<ProductImportReqDTO> importVOToReqDTO(List<ProductImportExcelVO> vo);
}
