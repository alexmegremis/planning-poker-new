package com.alexmegremis.planningpokerapi.api.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Builder
@Data
public class ResponseDTO<T> implements Serializable {
    private T body;
    private HttpStatus status;
    private String message;

    public static <T> ResponseDTO OK(T body) {
        return ResponseDTO.builder().body(body).status(HttpStatus.OK).message("Done").build();
    }

    public static <T> ResponseDTO CREATED(T body) {
        return ResponseDTO.builder().body(body).status(HttpStatus.CREATED).message("Created").build();
    }
}
