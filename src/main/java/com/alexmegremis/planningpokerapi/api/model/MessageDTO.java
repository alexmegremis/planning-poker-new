package com.alexmegremis.planningpokerapi.api.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Builder
public class MessageDTO<T> {

    private MessageType messageType;
    private final String TIMESTAMP = Instant.now().toString();

    private HttpStatus status;
    private String     message;

    private T payload;

    public static <T> MessageDTO CREATED(T payload, MessageType messageType) {
        return MessageDTO.builder().messageType(messageType).status(HttpStatus.CREATED).payload(payload).build();
    }
}
