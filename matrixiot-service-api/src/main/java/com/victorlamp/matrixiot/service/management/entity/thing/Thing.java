package com.victorlamp.matrixiot.service.management.entity.thing;

import com.victorlamp.matrixiot.service.management.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Thing implements Serializable {
    @Serial
    private static final long serialVersionUID = 2518609736289324060L;

    @Id
    private String id;
    @DBRef
    private Product product;
    private String rn;
    private String name;
    private String description;
    //    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoPoint position;
    private String gatewayId;
    //    private Integer status;
    private Boolean enabled; // false:停用, true:启用
    private Boolean online;  // false:离线, true:在线
    private List<String> tags;
    private ThingExternalConfig externalConfig;

    @CreatedDate
    private Long createdAt;      //创建时间
    private Long deletedAt;      //删除时间
    private Long connectedAt;    //连接时间
    private Long disconnectedAt; //断开时间
}
