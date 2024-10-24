package com.victorlamp.matrixiot.service.common.response;

import java.util.List;

public class ResponseDTOBuilder {

    public static <T> ResponseDTO<T> build(T data) {
        return ResponseDTO.success(data);
    }

    public static <T> PaginationResponseDTO<T> build(List<T> data, long total, int pageSize, int pageNum) {
        return PaginationResponseDTO.success(data, total, pageSize, pageNum);
    }
}
