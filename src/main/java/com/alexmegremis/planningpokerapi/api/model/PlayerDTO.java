package com.alexmegremis.planningpokerapi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Calendar;

@Data
@ToString
public class PlayerDTO {
    private String id;
    private String name;

    @Builder
    public PlayerDTO(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    @JsonIgnore
    private final Long created     = Calendar.getInstance().getTimeInMillis();
    @JsonIgnore
    private       Long lastContact = Calendar.getInstance().getTimeInMillis();
}
