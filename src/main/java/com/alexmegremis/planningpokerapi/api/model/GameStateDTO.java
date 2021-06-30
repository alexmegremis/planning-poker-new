package com.alexmegremis.planningpokerapi.api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GameStateDTO {

    private final SessionDTO    session;
    private final List<VoteDTO> votes;
    private final Boolean       votesOpen;
}
