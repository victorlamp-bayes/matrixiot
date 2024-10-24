package com.victorlamp.matrixiot.service.management.service;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.api.ProductService;
import com.victorlamp.matrixiot.service.management.api.ThingModelService;
import com.victorlamp.matrixiot.service.management.api.ThingService;
import com.victorlamp.matrixiot.service.management.convert.ProductConvert;
import com.victorlamp.matrixiot.service.management.dao.ExternalConfigTemplateRepository;
import com.victorlamp.matrixiot.service.management.dao.ProductRepository;
import com.victorlamp.matrixiot.service.management.dto.product.*;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigItem;
import com.victorlamp.matrixiot.service.management.entity.externalconfig.ExternalConfigTemplate;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import com.victorlamp.matrixiot.service.management.entity.product.ProductExternalConfig;
import com.victorlamp.matrixiot.service.management.entity.product.ProductStatistics;
import com.victorlamp.matrixiot.service.management.enums.CategoryEnum;
import com.victorlamp.matrixiot.service.management.enums.NetTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.NodeTypeEnum;
import com.victorlamp.matrixiot.service.management.enums.ProtocolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.victorlamp.matrixiot.common.exception.util.ServiceExceptionUtil.exception;
import static com.victorlamp.matrixiot.service.management.constant.ErrorCodeConstants.*;

@DubboService(validation = "true")
@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductRepository productRepository;
    @Resource
    private ExternalConfigTemplateRepository externalConfigTemplateRepository;
    @Resource
    private ThingModelService thingModelService;
    @Resource
    private ThingService thingService;
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    @Transactional(transactionManager = "mongoTransactionManager", rollbackFor = Exception.class)
    public Product createProduct(ProductCreateReqDTO reqDTO) {
        log.info("创建产品: {}", JSON.toJSONString(reqDTO));
        // 验证产品名称唯一
        validateProductNameUnique(null, reqDTO.getName());
        // 验证节点类型的有效性
        validateNodeType(reqDTO.getNodeType());
        // 验证网络类型和协议类型的有效性
        validateNetTypeAndProtocolType(reqDTO.getNodeType(), reqDTO.getNetType(), reqDTO.getProtocolType());
        // 验证产品分类的有效性
        validateCategory(reqDTO.getCategory());
        // 验证连网配置的有效性
        validateExternalConfig(reqDTO.getExternalConfig());

        Product product = ProductConvert.INSTANCE.toEntity(reqDTO);
        Product newProduct = productRepository.insert(product);
        thingModelService.createThingModel(newProduct.getId());
        return newProduct;
    }

    @Override
    public Product getProduct(String id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(transactionManager = "mongoTransactionManager", rollbackFor = Exception.class)
    public PageResult<Product> listProductPage(ProductPageReqDTO reqDTO) {
        if (StrUtil.isNotBlank(reqDTO.getNodeType())) {
            validateNodeType(reqDTO.getNodeType());
        }
        if (StrUtil.isNotBlank(reqDTO.getCategory())) {
            validateCategory(reqDTO.getCategory());
        }

        return productRepository.findPage(reqDTO);
    }

    @Override
    public List<Product> listAllProduct(ProductPageReqDTO reqDTO) {
        if (StrUtil.isNotBlank(reqDTO.getNodeType())) {
            validateNodeType(reqDTO.getNodeType());
        }
        if (StrUtil.isNotBlank(reqDTO.getCategory())) {
            validateCategory(reqDTO.getCategory());
        }

        return productRepository.findAll(reqDTO);
    }

    @Override
    public List<Product> listAllSimpleProducts() {
        return productRepository.findAllSimple();
    }

    @Override
    public void updateProduct(ProductUpdateReqDTO reqDTO) {
        Product product = validateProductExists(reqDTO.getId());

        // 已发布的产品: 只允许修改name, icon, description, category字段
        if (product.getPublished() != null && product.getPublished()) {
            if (!StrUtil.isNullOrUndefined(reqDTO.getName())) {
                validateProductNameUnique(reqDTO.getId(), reqDTO.getName());
                product.setName(reqDTO.getName());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getIcon())) {
                product.setIcon(reqDTO.getIcon());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getDescription())) {
                product.setDescription(reqDTO.getDescription());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getCategory())) {
                product.setCategory(reqDTO.getCategory());
            }

            if (reqDTO.getExternalConfig() != null) {
                if (!StrUtil.isNullOrUndefined(reqDTO.getExternalConfig().getType())) {
                    product.setExternalConfig(reqDTO.getExternalConfig());
                } else {
                    product.setExternalConfig(null);
                }
            }

            productRepository.save(product);
        } else {
            // 未发布产品 TODO 发布时检查字段是否完整  允许更新厂商、产品型号、外部配置
            if (!StrUtil.isNullOrUndefined(reqDTO.getName())) {
                validateProductNameUnique(reqDTO.getId(), reqDTO.getName());
                product.setName(reqDTO.getName());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getIcon())) {
                product.setIcon(reqDTO.getIcon());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getDescription())) {
                product.setDescription(reqDTO.getDescription());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getCategory())) {
                product.setCategory(reqDTO.getCategory());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getManufacturer())) {
                product.setManufacturer(reqDTO.getManufacturer());
            }
            if (!StrUtil.isNullOrUndefined(reqDTO.getModel())) {
                product.setModel(reqDTO.getModel());
            }

            if (reqDTO.getExternalConfig() != null) {
                if (!StrUtil.isNullOrUndefined(reqDTO.getExternalConfig().getType())) {
                    product.setExternalConfig(reqDTO.getExternalConfig());
                } else {
                    product.setExternalConfig(null);
                }
            }
            productRepository.save(product);
        }
    }

    @Override
    public void updateProductStatus(ProductUpdateStatusReqDTO reqDTO) {
        Product product = validateProductExists(reqDTO.getId());
        if (reqDTO.getPublished()) {
            if (product.getPublished()) {
                return;
            }
            // TODO 发布时检查字段是否完整
            product.setPublished(true);
            productRepository.save(product);
        } else {
            if (!product.getPublished()) {
                return;
            }
            // 如果产品下已存在设备，不能设置为未发布状态
            validateNotBindThingForUnpublish(reqDTO.getId());
            product.setPublished(false);
            productRepository.save(product);
        }
    }

    @Override
    public void deleteProduct(String id) {
        Product product = validateProductExists(id);

        if (product.getPublished() != null && product.getPublished()) {
            throw exception(PRODUCT_CANNOT_DELETE_BY_PUBLISHED);
        }

        validateNotBindThingForDelete(id);

        productRepository.deleteById(id);
    }

    @Override
    public void importProducts(List<ProductImportReqDTO> importProducts, boolean isUpdateSupport) {
        for (ProductImportReqDTO importProduct : importProducts) {
            // TODO 还应检查外部配置是否完全一致
            Product existProduct = productRepository.findByName(importProduct.getName());
            if (existProduct == null) {
                ProductCreateReqDTO createReqDTO = ProductConvert.INSTANCE.importToCreateDTO(importProduct);
                Product product = productService.createProduct(createReqDTO);
                thingModelService.updateThingModel(product.getId(), importProduct.getThingModel());
                continue;
            }
            if (!isUpdateSupport) {
                continue;
            }
            // 已发布的产品不允许通过导入更新
            if (existProduct.getPublished()) {
                continue;
            }

            ProductUpdateReqDTO updateReqDTO = ProductConvert.INSTANCE.importToUpdateDTO(importProduct);
            updateReqDTO.setId(existProduct.getId());
            productService.updateProduct(updateReqDTO);
            thingModelService.updateThingModel(existProduct.getId(), importProduct.getThingModel());
        }
    }

    @Override
    public List<Product> listProductByExternalType(String type) {
        return productRepository.findAllByExternalType(type);
    }

    @Override
    public List<ExternalConfigTemplate> listExternalTemplate() {
        return externalConfigTemplateRepository.findAll();
    }

    @Override
    public ExternalConfigTemplate getExternalTemplateByType(String type) {
        return externalConfigTemplateRepository.findByType(type);
    }

    @Override
    public List<ExternalConfigItem> listExternalConfigItem(String type) {
        return externalConfigTemplateRepository.findByType(type).getProduct();
    }

    @Override
    public ProductStatistics getStatistics() {
        ProductPageReqDTO reqDTO = new ProductPageReqDTO();
        int total = (int) productRepository.count(reqDTO);
        reqDTO.setPublished(true);
        int published = (int) productRepository.count(reqDTO);

        return new ProductStatistics(total, published);
    }

//
//    @Override
//    public List<EnumNameLabel> listNodeTypes() {
//        return listEnumNameLabel(NodeTypeEnum.class);
//    }
//
//    @Override
//    public List<EnumNameLabel> listNetTypes() {
//        return listEnumNameLabel(NetTypeEnum.class);
//    }
//
//    @Override
//    public List<EnumNameLabel> listProtocolTypes() {
//        return listEnumNameLabel(ProtocolTypeEnum.class);
//    }
//
//    @Override
//    public List<EnumNameLabel> listExternalConfigType() {
//        return listEnumNameLabel(ExternalConfigTypeEnum.class);
//    }
//
//    @Override
//    public List<EnumNameLabel> listCategories() {
//        return listEnumNameLabel(CategoryEnum.class);
//    }
//
//    @Override
//    public List<EnumNameLabel> listDataFormats() {
//        return listEnumNameLabel(DataFormatEnum.class);
//    }
//
//    @Override
//    public List<ProductDTO> listAllProductsByThirdPlatform(String thirdPlatform) {
//        List<Product> products = productRepository.findAllByThirdPlatform(thirdPlatform);
//        if (CollUtil.isEmpty(products)) {
//            log.info("产品不存在.thirdPlatform:[{}]", thirdPlatform);
//            return ListUtil.empty();
//        }
//
//        return ProductMapper.INSTANCE.toDTO(products);
//    }

    private Product validateProductExists(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw exception(PRODUCT_NOT_EXISTS);
        }
        return product;
    }

    private void validateProductNameUnique(String id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }

        Product product = productRepository.findByName(name);
        if (product == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同 id 的产品
        if (id == null) {
            throw exception(PRODUCT_NAME_DUPLICATE);
        }
        if (!product.getId().equals(id)) {
            throw exception(PRODUCT_NAME_DUPLICATE);
        }
    }

    private void validateNodeType(String nodeType) {
        if (StrUtil.isBlank(nodeType)) {
            throw exception(PRODUCT_NODE_TYPE_IS_NULL);
        }
        if (EnumUtil.notContains(NodeTypeEnum.class, nodeType)) {
            throw exception(PRODUCT_NODE_TYPE_INVALID);
        }
    }

    private void validateNetTypeAndProtocolType(String nodeType, String netType, String protocolType) {
        validateNodeType(nodeType);
        NodeTypeEnum nodeTypeEnum = EnumUtil.fromString(NodeTypeEnum.class, nodeType);
        switch (nodeTypeEnum) {
            case GATEWAY, DIRECT -> { // 直连设备/网关设备：netType不为空，protocolType为空
                validateNetType(netType);
                if (StrUtil.isNotBlank(protocolType)) {
                    throw exception(PRODUCT_DIRECT_DEVICE_CANNOT_CONFIG_PROTOCOL_TYPE);
                }
            }
            case SUBDEVICE -> { // 子设备：netType为空，protocolType不为空
                validateProtocolType(protocolType);
                if (StrUtil.isNotBlank(netType)) {
                    throw exception(PRODUCT_SUB_DEVICE_CANNOT_CONFIG_NET_TYPE);
                }
            }
            default -> {
                throw exception(PRODUCT_NODE_TYPE_INVALID);
            }
        }
    }

    private void validateNetType(String netType) {
        if (StrUtil.isBlank(netType)) {
            throw exception(PRODUCT_NET_TYPE_IS_NULL);
        }
        if (EnumUtil.notContains(NetTypeEnum.class, netType)) {
            throw exception(PRODUCT_NET_TYPE_INVALID);
        }
    }

    private void validateProtocolType(String protocolType) {
        if (StrUtil.isBlank(protocolType)) {
            throw exception(PRODUCT_PROTOCOL_TYPE_IS_NULL);
        }
        if (EnumUtil.notContains(ProtocolTypeEnum.class, protocolType)) {
            throw exception(PRODUCT_PROTOCOL_TYPE_INVALID);
        }
    }

    private void validateCategory(String category) {
        if (StrUtil.isBlank(category)) {
            throw exception(PRODUCT_CATEGORY_IS_NULL);
        }
        if (EnumUtil.notContains(CategoryEnum.class, category)) {
            throw exception(PRODUCT_CATEGORY_INVALID);
        }
    }

    private void validateExternalConfig(ProductExternalConfig externalConfig) {
        if (ObjUtil.isNull(externalConfig)) {
            return;
        }
//        String netConfigType = externalConfig.getType();
//        if (EnumUtil.notContains(ExternalConfigTypeEnum.class, netConfigType)) {
//            throw exception(PRODUCT_NET_CONFIG_TYPE_INVALID);
//        }
//        if (ObjUtil.isNull(externalConfig.getConfig())) {
//            throw exception(PRODUCT_NET_CONFIG_IS_EMPTY);
//        }

        // TODO 严格检查每种类别的网络配置字段
    }

    private void validateNotBindThingForUnpublish(String productId) {
        boolean bind = thingService.existsByProductId(productId);
        if (bind) {
            throw exception(PRODUCT_CANNOT_UNPUBLISHED_BY_BIND_THING);
        }
    }

    private void validateNotBindThingForDelete(String productId) {
        boolean bind = thingService.existsByProductId(productId);
        if (bind) {
            throw exception(PRODUCT_CANNOT_DELETE_BY_BIND_THING);
        }
    }
}