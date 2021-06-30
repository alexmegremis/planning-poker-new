package com.alexmegremis.planningpokerapi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Calendar;

@Data
@ToString
public class SessionDTO implements UniqueIdentifiable {

    private String id;
    private String name;
    private String password;
    private PlayerDTO owner;

    @Builder
    public SessionDTO(final String id, final String name, final String password, final PlayerDTO owner) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.owner = owner;
    }

    @JsonIgnore
    private final Calendar created     = Calendar.getInstance();
    @JsonIgnore
    private       Calendar lastContact = Calendar.getInstance();
}
