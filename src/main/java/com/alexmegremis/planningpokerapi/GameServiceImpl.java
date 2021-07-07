package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

import static com.alexmegremis.planningpokerapi.GameDataAware.*;

@Slf4j
@Service
public class GameServiceImpl implements GameService, GameDataAware {

    public static final String FLAG_VOTING_OPEN = "votingOpen";
    public static final String FLAG_VOTES_VISIBLE = "votesVisible";
    public static final String FLAG_PLAYERS_VISIBLE = "playersVisible";

    private MessageType validateBasicVoteActivity(Optional<PlayerDTO> playerSearch, Optional<SessionDTO> sessionSearch) {
        MessageType result = null;
        if (playerSearch.isEmpty()) {
            result = MessageType.FAIL_VOTE_PLAYER_NOT_FOUND;
        } else if (sessionSearch.isEmpty()) {
            result = MessageType.FAIL_VOTE_SESSION_NOT_FOUND;
        }
        return result;
    }

    private MessageType validateVoteManagementActivity(Optional<PlayerDTO> playerSearch, Optional<SessionDTO> sessionSearch) {
        MessageType result = validateBasicVoteActivity(playerSearch, sessionSearch);
        if(result == null) {
            if(sessionSearch.get().getOwner() != playerSearch.get()) {
                result = MessageType.FAIL_VOTE_MANAGEMENT_NOT_OWNER;
            }
        }
        return result;
    }

    @Override
    public MessageType vote(final VoteDTO message, final String userSessionId) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(message.getSession().getId());

        PlayerDTO   player  = null;
        SessionDTO  session = null;

        MessageType result  = validateBasicVoteActivity(playerSearch, sessionSearch);

        if(result == null) {
            player = playerSearch.get();
            session = sessionSearch.get();
            if (! session.getPlayers().contains(player)) {
                result = MessageType.FAIL_VOTE_PLAYER_NOT_IN_SESSION;
            }
        }

        if (result == null) {
            session.getVotes().put(player, message.getVote());
            if(!session.getVotesAlwaysVisible()) {
                session.setVotesVisible(false);
            }
            session.updated();
            result = MessageType.VOTE_ACK;
            log.info(">>> Player {} voted {} for session {}", player, message.getVote(), session);
        }

        return result;
    }

    private <T> MessageDTO<SessionUpdateDTO> setManagementFlag(final String gameSessionID, final String userSessionId, final String flagName, final Class<T> flagType, final T flagValue) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType messageType  = validateVoteManagementActivity(playerSearch, sessionSearch);
        SessionUpdateDTO update = null;
        if(messageType == null) {
            SessionDTO session = sessionSearch.get();
            doSetManagementFlag(session, flagName, flagType, flagValue);
            if(flagName.equals(FLAG_VOTING_OPEN) && flagType == Boolean.class && (Boolean) flagValue && !session.getVotesAlwaysVisible()) {
                doSetManagementFlag(session, FLAG_VOTES_VISIBLE, Boolean.class, false);
            }
            messageType = MessageType.VOTE_UPDATE;
            update = SessionUpdateDTO.create(sessionSearch.get());
        }

        final MessageDTO<SessionUpdateDTO> result = MessageDTO.<SessionUpdateDTO>builder().messageType(messageType).payload(update).build();
        return result;
    }

    @SneakyThrows
    private <T> void doSetManagementFlag(final SessionDTO session, final String flagName, final Class<T> type, final T flagValue) {
        final Method method = Arrays.stream(session.getClass().getDeclaredMethods()).filter(m -> m.getName().equalsIgnoreCase(("set" + flagName)) && m.getParameterTypes()[0] == type).findFirst().get();
        method.invoke(session, flagValue);
        log.info(">>> Setting flag {} to {} for session {} ({})", flagName, flagValue, session.getName(), session.getId());
    }

    @Override
    public MessageDTO<SessionUpdateDTO> openVoting(final String gameSessionID, final String userSessionId) {
        return setManagementFlag(gameSessionID, userSessionId, FLAG_VOTING_OPEN, Boolean.class, true);
    }

    @Override
    public MessageDTO<SessionUpdateDTO> closeVoting(final String gameSessionID, final String userSessionId) {
        return setManagementFlag(gameSessionID, userSessionId, FLAG_VOTING_OPEN, Boolean.class, false);
    }

    @Override
    public MessageDTO<SessionUpdateDTO> resetVoting(final String gameSessionID, final String userSessionId) {
        return null;
    }

    @Override
    public MessageDTO<SessionUpdateDTO> showVotes(final String gameSessionID, final String userSessionId) {
        return setManagementFlag(gameSessionID, userSessionId, FLAG_VOTES_VISIBLE, Boolean.class, true);
    }

    @Override
    public MessageDTO<SessionUpdateDTO> hideVotes(final String gameSessionID, final String userSessionId) {
        return setManagementFlag(gameSessionID, userSessionId, FLAG_VOTES_VISIBLE, Boolean.class, false);
    }

    @Override
    public MessageDTO<SessionUpdateDTO> showPlayers(final String gameSessionID, final String userSessionId) {
        return setManagementFlag(gameSessionID, userSessionId, FLAG_PLAYERS_VISIBLE, Boolean.class, true);
    }

    @Override
    public MessageDTO<SessionUpdateDTO> hidePlayers(final String gameSessionID, final String userSessionId) {
        return setManagementFlag(gameSessionID, userSessionId, FLAG_PLAYERS_VISIBLE, Boolean.class, false);
    }

    @Override
    public MessageDTO<SessionUpdateDTO> getSessionUpdate(final String gameSessionID, final String userSessionId) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType messageType = validateBasicVoteActivity(playerSearch, sessionSearch);
        SessionUpdateDTO update = null;
        if(messageType == null) {
            messageType = MessageType.VOTE_UPDATE;
            update = SessionUpdateDTO.create(sessionSearch.get());
        }

        final MessageDTO<SessionUpdateDTO> result = MessageDTO.<SessionUpdateDTO>builder().messageType(messageType).payload(update).build();
        return result;
    }

    @Override
    public MessageDTO<SessionUpdateDTO> getSessionUpdate(final String gameSessionID) {

        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType messageType = null;
        SessionUpdateDTO update = null;

        if(sessionSearch.isPresent()) {
            messageType = MessageType.VOTE_UPDATE;
            update = SessionUpdateDTO.create(sessionSearch.get());
        } else {
            messageType = MessageType.FAIL_JOIN_SESSION_SESSION_NOT_FOUND;
        }

        final MessageDTO<SessionUpdateDTO> result = MessageDTO.<SessionUpdateDTO>builder().messageType(messageType).payload(update).build();
        return result;
    }

    public <T extends UniqueIdentifiable> String getUniqueId(final Collection<T> existing) {

        String newId;

        do {
            newId = String.valueOf(Math.toIntExact(Math.round(Math.random() * ((9999999 - 1000000) + 1)) + 1000000));
        } while (exists(newId, existing));

        return newId;
    }

    @Override
    public PlayerDTO createPlayer(final PlayerDTO message, final String userSessionId) {
        message.setId(getUniqueId(PLAYERS));
        message.setSessionID(userSessionId);
        PLAYERS.add(message);

        log.info(">>> created player via WS: {}", message);
        return message;
    }

    @Override
    public MessageDTO<SessionUpdateDTO> createSession(final SessionDTO message, final String userSessionId) {
        message.setId(getUniqueId(SESSIONS));
        PlayerDTO owner = findPlayer(userSessionId).get();
        message.setOwner(owner);
        message.getPlayers().add(owner);
        if(message.getOwnerCanVote()) {
            initiateFirstVote(message, owner);
        }
        SESSIONS.add(message);

        SessionUpdateDTO update = SessionUpdateDTO.create(message);
        MessageDTO<SessionUpdateDTO> result = MessageDTO.CREATED(update, MessageType.CREATED_SESSION);
        log.info(">>> created session via WS: {}", message);

        return result;
    }

    @Override
    public MessageDTO<SessionUpdateDTO> joinSession(final SessionDTO message, final String userSessionId) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(message.getId());

        MessageType messageType = null;
        MessageDTO<SessionUpdateDTO> result = null;
        if (playerSearch.isEmpty()) {
            messageType = MessageType.FAIL_JOIN_SESSION_PLAYER_NOT_FOUND;
        } else if (sessionSearch.isEmpty()) {
            messageType = MessageType.FAIL_JOIN_SESSION_SESSION_NOT_FOUND;
        } else if (StringUtils.hasLength(sessionSearch.get().getPassword()) && ! Objects.equals(sessionSearch.get().getPassword(), message.getPassword())) {
            messageType = MessageType.FAIL_JOIN_SESSION_AUTH_FAIL;
        } else {
            PlayerDTO  player  = playerSearch.get();
            SessionDTO session = sessionSearch.get();
            if (! session.getPlayers().contains(player)) {
                session.getPlayers().add(player);
                initiateFirstVote(session, player);

                log.info(">>> player {} joined session via WS: {}", player, session);
            } else {
                log.info(">>> player {} had already joined session via WS: {}", player, session);
            }

            result = MessageDTO.<SessionUpdateDTO>builder().messageType(MessageType.JOINED_SESSION).payload(SessionUpdateDTO.create(session)).build();
        }
        if(result == null) {
            result = MessageDTO.<SessionUpdateDTO>builder().messageType(messageType).build();
        }

        return result;
    }

    private void initiateFirstVote(final SessionDTO session, final PlayerDTO player) {
        session.getVotes().put(player, "");
        session.updated();
    }

    private <T extends UniqueIdentifiable> boolean exists(final String id, final Collection<T> existing) {
        return existing.stream().anyMatch(i -> i.getId().equals(id));
    }
}
