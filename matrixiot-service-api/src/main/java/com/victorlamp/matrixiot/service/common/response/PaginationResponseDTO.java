package com.victorlamp.matrixiot.service.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponseDTO<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 6309715817162878934L;

    private boolean success;
    private List<T> data;
    private Long total;
    private Integer pageSize;
    private Integer pageNum;
    private Integer errcode;
    private String errmsg;

    private PaginationResponseDTO(List<T> data) {
        this.success = true;
        this.data = data;
    }

    private PaginationResponseDTO(List<T> data, Long total, Integer pageSize, Integer pageNum) {
        this.success = true;
        this.data = data;
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }

    private PaginationResponseDTO(Integer errcode, String errmsg) {
        this.success = false;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public static <T> PaginationResponseDTO<T> success() {
        return new PaginationResponseDTO<>(null);
    }

    public static <T> PaginationResponseDTO<T> success(List<T> data) {
        return new PaginationResponseDTO<>(data);
    }

    public static <T> PaginationResponseDTO<T> success(List<T> data, Long total, Integer pageSize, Integer pageNum) {
        return new PaginationResponseDTO<>(data, total, pageSize, pageNum);
    }

    public static <T> PaginationResponseDTO<T> fail() {
        return new PaginationResponseDTO<>(null, null);
    }

    public static <T> PaginationResponseDTO<T> fail(Integer errcode, String errmsg) {
        return new PaginationResponseDTO<>(errcode, errmsg);
    }
}
