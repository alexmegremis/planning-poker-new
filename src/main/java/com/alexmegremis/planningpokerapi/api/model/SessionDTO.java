package com.alexmegremis.planningpokerapi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Calendar;

@Data
@ToString
public class SessionDTO {
    private String id;
    private String name;
    private String password;

    @Builder
    public SessionDTO(final String id, final String name, final String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    @JsonIgnore
    private final Long created     = Calendar.getInstance().getTimeInMillis();
    @JsonIgnore
    private       Long lastContact = Calendar.getInstance().getTimeInMillis();
}
