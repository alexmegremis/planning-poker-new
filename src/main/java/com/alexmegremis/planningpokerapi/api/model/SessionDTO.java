package com.alexmegremis.planningpokerapi.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;
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
    private Boolean   ownerCanVote;
    private Boolean   playersVisible = true;
    private Boolean   votesVisible   = true;
    private Boolean   votingOpen     = false;

    private long      version = 1;

    public void updated() {
        this.version++;
        updated = ZonedDateTime.now();
    }
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
    private final ZonedDateTime created = ZonedDateTime.now();
    public ZonedDateTime getCreated() {
        return ZonedDateTime.ofInstant(created.toInstant(), created.getZone());
    }
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ZonedDateTime updated = ZonedDateTime.now();
    public final ZonedDateTime getUpdated() {
        return ZonedDateTime.ofInstant(updated.toInstant(), updated.getZone());
    }
    private void setUpdated(final ZonedDateTime updated) {

    }
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final List<PlayerDTO>        players = new CopyOnWriteArrayList<>();
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Map<PlayerDTO, String> votes   = Collections.synchronizedMap(new LinkedHashMap<>());
}
