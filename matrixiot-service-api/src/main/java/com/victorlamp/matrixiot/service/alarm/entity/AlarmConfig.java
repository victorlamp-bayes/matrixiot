package com.victorlamp.matrixiot.service.alarm.entity;

import com.victorlamp.matrixiot.service.alarm.enums.AlarmLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class AlarmConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = -7669745587861071357L;

    @Id
    private String id;
    private String productId;
    private String description;
    private AlarmLevelEnum level;
    private List<ContactInfo> contacts; //联系人&电话&邮箱
    @CreatedDate
    private Long createdAt;
    private Long deletedAt;

}
