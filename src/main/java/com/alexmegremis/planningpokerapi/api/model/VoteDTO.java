package com.alexmegremis.planningpokerapi.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteDTO {
    private PlayerDTO player;
    private SessionDTO session;
    private String vote;
}
