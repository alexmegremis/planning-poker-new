package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public interface GameDataAware {

    List<SessionDTO> SESSIONS = new CopyOnWriteArrayList<>();
    List<PlayerDTO>  PLAYERS  = new CopyOnWriteArrayList<>();

    static Optional<PlayerDTO> findPlayer(final String userSessionID) {
        Optional<PlayerDTO> result = PLAYERS.stream().filter(p -> p.getSessionID().equals(userSessionID)).findFirst();
        return result;
    }

    static Optional<SessionDTO> findSession(final String sessionID) {
        Optional<SessionDTO> result = SESSIONS.stream().filter(s -> s.getId().equals(sessionID)).findAny();
        return result;
    }
}
