package com.victorlamp.matrixiot.service.management.api;

import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.dto.product.*;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigItem;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigTemplate;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.product.ProductStatistics;

import java.util.List;

public interface ProductService {

    Product createProduct(ProductCreateReqDTO reqDTO);

    Product getProduct(String id);

    PageResult<Product> listProductPage(ProductPageReqDTO reqDTO);

    List<Product> listAllProduct(ProductPageReqDTO reqDTO);

    List<Product> listAllSimpleProducts();

    void updateProduct(ProductUpdateReqDTO reqDTO);

    void updateProductStatus(ProductUpdateStatusReqDTO reqDTO);

    void deleteProduct(String id);

    void importProducts(List<ProductImportReqDTO> importProducts, boolean isUpdateSupport);

    List<Product> listProductByExternalType(String type);

    List<ExternalConfigTemplate> listExternalTemplate();

    ExternalConfigTemplate getExternalTemplateByType(String type);

    List<ExternalConfigItem> listExternalConfigItem(String type);

    ProductStatistics getStatistics();

//    List<EnumNameLabel> listNodeTypes();
//
//    List<EnumNameLabel> listNetTypes();
//
//    List<EnumNameLabel> listProtocolTypes();
//
//    List<EnumNameLabel> listExternalConfigType();
//
//    List<EnumNameLabel> listCategories();
//
//    List<EnumNameLabel> listDataFormats();

//    List<ProductDTO> listAllProductsByThirdPlatform(String thirdPlatform);
}
