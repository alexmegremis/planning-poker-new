package com.alexmegremis.planningpokerapi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Calendar;

@Data
@ToString
@EqualsAndHashCode
public class PlayerDTO implements UniqueIdentifiable {

    private String id;
    private String name;

    @Builder
    public PlayerDTO(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private final Long   created     = Calendar.getInstance().getTimeInMillis();
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private       Long   lastContact = Calendar.getInstance().getTimeInMillis();
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private       String sessionID;
}
