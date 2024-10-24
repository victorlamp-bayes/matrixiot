package com.victorlamp.matrixiot.service.management.controller.product;

import cn.hutool.core.map.MapUtil;
import com.victorlamp.matrixiot.common.pojo.CommonResult;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.common.util.enums.EnumUtils;
import com.victorlamp.matrixiot.common.util.excel.ExcelUtils;
import com.victorlamp.matrixiot.service.common.validation.annotation.IdHex24;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductCreateReqVO;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductImportExcelVO;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductSimpleRespVO;
import com.victorlamp.matrixiot.service.management.controller.product.vo.ProductUpdateStatusReqVO;
import com.victorlamp.matrixiot.service.management.convert.ProductConvert;
import com.victorlamp.matrixiot.service.management.dto.product.ProductPageReqDTO;
import com.victorlamp.matrixiot.service.management.dto.product.ProductUpdateReqDTO;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigItem;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.product.ProductStatistics;
import com.victorlamp.matrixiot.service.management.enums.*;
import com.victorlamp.matrixiot.service.management.utils.excel.ProductImportExcelVOListener;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.common.pojo.CommonResult.success;
import static com.victorlamp.matrixiot.service.management.constant.DictTypeConstant.*;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.PRODUCT_IMPORT_FILE_SIZE_LT_10M;

@OpenAPIDefinition
@Tag(name = "设备管理 - 产品")
@RestController
@Validated
@RequestMapping(value = "/api/v1/iot/product", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    @Operation(summary = "创建产品")
    @PostMapping("create")
    public CommonResult<Product> createProduct(@RequestBody @Valid ProductCreateReqVO reqVO) {
        Product product = productService.createProduct(ProductConvert.INSTANCE.toDTO(reqVO));
        return success(product);
    }

    @Operation(summary = "查询产品详情")
    @GetMapping("describe")
    public CommonResult<Product> describeProduct(@RequestParam("id") @IdHex24 String id) {
        Product product = productService.getProduct(id);
        return success(product);
    }

    @Operation(summary = "获取产品列表")
    @GetMapping("list")
    public CommonResult<PageResult<Product>> listProducts(ProductPageReqDTO reqDTO) {
        PageResult<Product> productPageResult = productService.listProductPage(reqDTO);
        return success(productPageResult);
    }

    @Operation(summary = "获取所有产品列表精简信息")
    @GetMapping("list-all-simple")
    public CommonResult<List<ProductSimpleRespVO>> listAllSimpleProducts() {
        List<Product> allProducts = productService.listAllSimpleProducts();
        return success(ProductConvert.INSTANCE.toSimpleList(allProducts));
    }

    @Operation(summary = "更新产品")
    @PatchMapping("update")
    public CommonResult<Boolean> updateProduct(@RequestBody @Valid ProductUpdateReqDTO reqDTO) {
        productService.updateProduct(reqDTO);
        return success(true);
    }

    @Operation(summary = "更新产品状态")
    @PatchMapping("update-status")
    public CommonResult<Boolean> updateProductStatus(@RequestBody @Valid ProductUpdateStatusReqVO reqVO) {
        productService.updateProductStatus(ProductConvert.INSTANCE.toDTO(reqVO));
        return success(true);
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("delete")
    public CommonResult<Boolean> deleteProduct(@RequestParam("id") @IdHex24 @NotBlank String id) {
        productService.deleteProduct(id);
        return success(true);
    }

    @Operation(summary = "导入产品和相应的物模型")
    @PostMapping("import")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true),
    })
    public CommonResult<Boolean> importProducts(@RequestParam("file") MultipartFile file,
                                                @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws IOException {
        if (file.getSize() > 10 * 1024 * 1024) {
            throw exception(PRODUCT_IMPORT_FILE_SIZE_LT_10M);
        }

        List<ProductImportExcelVO> excelList = ExcelUtils.read(file, ProductImportExcelVO.class, 1, new ProductImportExcelVOListener());
        productService.importProducts(ProductConvert.INSTANCE.importVOToReqDTO(excelList), updateSupport);
        return success(true);
    }

//    @Operation(summary = "导出产品和相应的物模型")
//    @GetMapping("export")
//    public ResponseDTO exportProduct(HttpServletResponse response) throws IOException {
//        List<ProductExportVO> list = productUtils.prepareProductExportData();
//        ExcelUtils.write(response, "产品文档.xls", "产品列表", ProductExportVO.class,
//                BeanUtils.toBean(list, ProductExportVO.class));
//        return ResponseDTO.success();
//    }

    @Operation(summary = "获取产品数据词典")
    @GetMapping("dict-data/list")
    CommonResult<Map<String, List<Object>>> listDictData() {
        Map<String, List<Object>> map = MapUtil.newHashMap();
        map.put(NODE_TYPE, EnumUtils.listEnumValue(NodeTypeEnum.class));
        map.put(NET_TYPE, EnumUtils.listEnumValue(NetTypeEnum.class));
        map.put(PROTOCOL_TYPE, EnumUtils.listEnumValue(ProtocolTypeEnum.class));
        map.put(DATA_FORMAT, EnumUtils.listEnumValue(DataFormatEnum.class));
        map.put(CATEGORY, EnumUtils.listEnumValue(CategoryEnum.class));
        return success(map);
    }

    @Operation(summary = "获取产品外部类型列表")
    @GetMapping("external-type/list")
    CommonResult<List<Object>> listExternalType() {
        return success(EnumUtils.listEnumValue(ExternalTypeEnum.class));
    }

    @Operation(summary = "获取产品外部配置项列表")
    @GetMapping("external-config-item/list")
    CommonResult<List<ExternalConfigItem>> listExternalConfigItem(String type) {
        return success(productService.listExternalConfigItem(type));
    }

    @Operation(summary = "获取产品统计信息")
    @GetMapping("statistics")
    CommonResult<ProductStatistics> getThingStatistics() {
        return success(productService.getStatistics());
    }
}
