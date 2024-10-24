package com.victorlamp.matrixiot.service.management.dao;

import cn.hutool.core.date.DateUtil;
import com.victorlamp.matrixiot.common.pojo.PageResult;
import com.victorlamp.matrixiot.service.management.dto.product.ProductPageReqDTO;
import com.victorlamp.matrixiot.service.management.entity.product.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    String PRODUCT_QUERY = """
            {
              $and: [
                {
                  $expr: {
                    $cond: [
                      { $in: [:#{#keywords}, [null, '']] },
                      {},
                      {
                        $or: [
                          { '$regexMatch': { input: {$toString: '$_id'}, regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$name', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$description', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$manufacturer', regex: :#{#keywords}, options:'i' } },
                          { '$regexMatch': { input: '$model', regex: :#{#keywords}, options:'i' } }
                        ],
                      }
                    ]
                  }
                },
                { $expr: { $cond: [ { $in: [:#{#netType}, [null, '']] }, {}, { $eq: ['$netType', :#{#netType}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#nodeType}, [null, '']] }, {}, { $eq: ['$nodeType', :#{#nodeType}] } ] } },
                { $expr: { $cond: [ { $in: [:#{#category}, [null, '']] }, {}, { $eq: ['$category', :#{#category}] } ] } },
                { $expr: { $cond: [ { $eq: [:#{#published}, null] }, {}, { $eq: ['$published', :#{#published}] } ] } }
              ],
              'deletedAt': null
            }
            """;

    @Override
    @NonNull
    @Query("{'id': :#{#id}, 'deletedAt': null}")
    Optional<Product> findById(@NonNull String id);

    @Override
    @NonNull
    @Query("{'deletedAt': null}")
    Page<Product> findAll(@NonNull Pageable pageable);

    @Override
    @CountQuery("{'deletedAt': null}")
    long count();

    @Override
    default void deleteById(@NonNull String id) {
        Product product = findById(id).orElse(null);
        if (product == null) {
            return;
        }
        product.setDeletedAt(DateUtil.current());
        save(product);
    }

    @Query("{'name' : :#{#name},'deletedAt': null}")
    Product findByName(@NonNull String name);

    @Query(value = "{'deletedAt': null}", fields = "{ 'id': 1, 'name': 1, 'nodeType': 1 , 'externalConfig.type': 1}")
    List<Product> findAllSimple();

    default List<Product> findAll(ProductPageReqDTO reqDTO) {
        return findAll(reqDTO.getKeywords(), reqDTO.getNetType(), reqDTO.getNodeType(), reqDTO.getCategory(), reqDTO.getPublished());
    }

    @Query(PRODUCT_QUERY)
    List<Product> findAll(String keywords, String netType, String nodeType, String category, Boolean published);

    default PageResult<Product> findPage(ProductPageReqDTO reqDTO) {
        PageRequest pageRequest = PageRequest.of(reqDTO.getPageNo() - 1, reqDTO.getPageSize());
        Page<Product> productPage = findPage(reqDTO.getKeywords(), reqDTO.getNetType(), reqDTO.getNodeType(), reqDTO.getCategory(), reqDTO.getPublished(), pageRequest);
        long total = count(reqDTO);
        return new PageResult<>(productPage.toList(), total, productPage.getNumber() + 1, productPage.getSize());
    }

    @Query(PRODUCT_QUERY)
    Page<Product> findPage(String keywords, String netType, String nodeType, String category, Boolean published, Pageable pageable);

    default long count(ProductPageReqDTO reqDTO) {
        return count(reqDTO.getKeywords(), reqDTO.getNodeType(), reqDTO.getCategory(), reqDTO.getPublished());
    }

    @CountQuery(PRODUCT_QUERY)
    long count(String keywords, String nodeType, String category, Boolean published);

    @Query("{ 'externalConfig.type' : :#{#externalType}, 'deletedAt': null }")
    List<Product> findAllByExternalType(String externalType);
}
