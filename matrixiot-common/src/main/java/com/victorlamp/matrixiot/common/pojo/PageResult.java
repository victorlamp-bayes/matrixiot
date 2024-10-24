package com.victorlamp.matrixiot.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "分页结果")
@Data
public final class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -596008388958133828L;

    @Schema(description = "数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long total;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNo;

    @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total, PageParam pageParam) {
        this(list, total, pageParam.getPageNo(), pageParam.getPageSize());
    }

    public PageResult(List<T> list, Long total, Integer pageNo, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }
}
