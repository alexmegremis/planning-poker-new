package com.alexmegremis.planningpokerapi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@ToString
public class SessionDTO implements UniqueIdentifiable {

    private String    id;
    private String    name;
    @JsonProperty (access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String    password;
    private PlayerDTO owner;
    private boolean   ownerCanVote;
    private boolean   playersVisible = true;
    private boolean   votingOpen = false;

    @Builder
    public SessionDTO(final String id, final String name, final String password, final PlayerDTO owner,
                      final boolean ownerCanVote, final boolean playersVisible) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.owner = owner;
        this.ownerCanVote = ownerCanVote;
        this.playersVisible = playersVisible;
    }

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Calendar created     = Calendar.getInstance();
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private       Calendar lastContact = Calendar.getInstance();

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final List<PlayerDTO>        players = new CopyOnWriteArrayList<>();
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Map<PlayerDTO, String> votes   = Collections.synchronizedMap(new LinkedHashMap<>());
}
