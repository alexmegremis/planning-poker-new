package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
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
    public MessageType vote(final VoteDTO message, final String sessionId) {

        Optional<PlayerDTO> playerSearch = PLAYERS.stream().filter(p -> p.getSessionID().equals(sessionId)).findFirst();
        Optional<SessionDTO> sessionSearch = SESSIONS.stream().filter(s -> s.getId().equals(message.getSession().getId())).findAny();

        MessageType result = null;
        PlayerDTO player = null;
        SessionDTO session = null;

        if(playerSearch.isEmpty()) {
            result = MessageType.FAIL_VOTE_PLAYER_NOT_FOUND;
        } else if(sessionSearch.isEmpty()) {
            result = MessageType.FAIL_VOTE_SESSION_NOT_FOUND;
        } else {
            player = playerSearch.get();
            session = sessionSearch.get();
            if (! session.getPlayers().contains(player)) {
                result = MessageType.FAIL_VOTE_PLAYER_NOT_IN_SESSION;
            }
        }

        if(result == null) {
            session.getVotes().put(player, message.getVote());
            result = MessageType.VOTE_ACK;
            log.info(">>> Player {} voted {} for session {}", player, message.getVote(), session);
        }

        return result;
    }

    @Override
    public SessionDTO createSession(final SessionDTO message, final String sessionId) {
        message.setId(getUniqueId(SESSIONS));
        PlayerDTO owner = PLAYERS.stream().filter(p -> p.getSessionID().equals(sessionId)).findFirst().get();
        message.setOwner(owner);
        message.getPlayers().add(owner);
        SESSIONS.add(message);

        log.info(">>> created session via WS: {}", message);

        return message;
    }

    @Override
    public MessageType joinSession(final SessionDTO message, final String sessionId) {

        Optional<PlayerDTO> playerSearch = PLAYERS.stream().filter(p -> p.getSessionID().equals(sessionId)).findFirst();
        Optional<SessionDTO> sessionSearch = SESSIONS.stream().filter(s -> s.getId().equals(message.getId())).findAny();

        MessageType result = null;

        if(playerSearch.isEmpty()) {
            result = MessageType.FAIL_JOIN_SESSION_PLAYER_NOT_FOUND;
        } else if(sessionSearch.isEmpty()) {
            result = MessageType.FAIL_JOIN_SESSION_SESSION_NOT_FOUND;
        } else if(StringUtils.hasLength(sessionSearch.get().getPassword()) && !Objects.equals(sessionSearch.get().getPassword(), message.getPassword())) {
            result = MessageType.FAIL_JOIN_SESSION_AUTH_FAIL;
        } else {
            PlayerDTO player = playerSearch.get();
            SessionDTO session = sessionSearch.get();
            if(!session.getPlayers().contains(player)) {
                session.getPlayers().add(player);
                log.info(">>> player {} joined session via WS: {}", player, session);
            } else {
                log.info(">>> player {} had already joined session via WS: {}", player, session);
            }
            message.setOwner(session.getOwner());
            result = MessageType.JOINED_SESSION;
        }

        return result;
    }

    private <T extends UniqueIdentifiable> boolean exists(final String id, final Collection<T> existing) {
        return existing.stream().anyMatch(i -> i.getId().equals(id));
    }
}
