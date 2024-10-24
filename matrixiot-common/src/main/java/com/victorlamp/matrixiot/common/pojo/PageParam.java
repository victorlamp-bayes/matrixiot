package com.victorlamp.matrixiot.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Schema(description = "分页参数")
@Data
public class PageParam implements Serializable {

    /**
     * 每页条数 - 不分页
     * <p>
     * 例如说，导出接口，可以设置 {@link #pageSize} 为 -1 不分页，查询所有数据。
     */
    public static final Integer PAGE_SIZE_NONE = -1;
    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 20;

    @Serial
    private static final long serialVersionUID = -7273225252800072488L;

    @Schema(description = "页码，从 1 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = PAGE_NO;

    @Schema(description = "每页条数，最大值为 100", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 10, message = "每页条数最小值为 10")
    @Max(value = 100, message = "每页条数最大值为 100")
    private Integer pageSize = PAGE_SIZE;

}
