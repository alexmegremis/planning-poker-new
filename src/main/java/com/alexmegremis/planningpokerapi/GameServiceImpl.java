package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class GameServiceImpl implements GameService {

    public static final List<SessionDTO> SESSIONS = new CopyOnWriteArrayList<>();
    public static final List<PlayerDTO>  PLAYERS  = new CopyOnWriteArrayList<>();

    public <T extends UniqueIdentifiable> String getUniqueId(final Collection<T> existing) {

        String newId;

        do {
            newId = String.valueOf(Math.toIntExact(Math.round(Math.random() * ((9999999 - 1000000) + 1)) + 1000000));
        } while (exists(newId, existing));

        return newId;
    }

    @Override
    public PlayerDTO createPlayer(final PlayerDTO message, final String sessionId) {
        message.setId(getUniqueId(PLAYERS));
        message.setSessionID(sessionId);
        PLAYERS.add(message);

        log.info(">>> created player via WS: {}", message);
        return message;
    }

    @Override
    public SessionDTO createSession(final SessionDTO message, final String sessionId) {
        message.setId(getUniqueId(SESSIONS));
        PlayerDTO owner = PLAYERS.stream().filter(p -> p.getSessionID().equals(sessionId)).findFirst().get();
        message.setOwner(owner);
        SESSIONS.add(message);

        SessionDTO result = SessionDTO.builder().id(message.getId()).name(message.getName()).owner(message.getOwner()).ownerCanVote(message.isOwnerCanVote()).build();
        log.info(">>> created session via WS: {}", result);

        return result;
    }

    private <T extends UniqueIdentifiable> boolean exists(final String id, final Collection<T> existing) {
        return existing.stream().anyMatch(i -> i.getId().equals(id));
    }
}
