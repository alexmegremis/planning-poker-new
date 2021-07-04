package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.alexmegremis.planningpokerapi.GameDataAware.*;

@Slf4j
@Service
public class GameServiceImpl implements GameService, GameDataAware {

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
            result = MessageType.VOTE_ACK;
            log.info(">>> Player {} voted {} for session {}", player, message.getVote(), session);
        }

        return result;
    }

    @Override
    public MessageType openVoting(final String gameSessionID, final String userSessionId) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType result  = validateVoteManagementActivity(playerSearch, sessionSearch);

        if(result == null) {
            sessionSearch.get().setVotingOpen(true);
            result = MessageType.VOTE_OPEN;
        }
        return result;
    }

    @Override
    public MessageType closeVoting(final String gameSessionID, final String userSessionId) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType result  = validateVoteManagementActivity(playerSearch, sessionSearch);

        if(result == null) {
            sessionSearch.get().setVotingOpen(false);
            result = MessageType.VOTE_CLOSE;
        }
        return result;
    }

    @Override
    public MessageType resetVoting(final String gameSessionID, final String userSessionId) {
        return null;
    }

    @Override
    public MessageType getVotesInSession(final String gameSessionID, final String userSessionId, final Map<String, String> votes) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType result = validateBasicVoteActivity(playerSearch, sessionSearch);

        if(result == null) {
            doGetVotesInSession(sessionSearch.get(), votes);
            result = MessageType.VOTE_UPDATE;
        }
        return result;
    }

    @Override
    public MessageType getVotesInSession(final String gameSessionID, final Map<String, String> votes) {

        Optional<SessionDTO> sessionSearch = findSession(gameSessionID);

        MessageType result = null;

        if(sessionSearch.isPresent()) {
            doGetVotesInSession(sessionSearch.get(), votes);
            result = MessageType.VOTE_UPDATE;
        } else {
            result = MessageType.FAIL_JOIN_SESSION_SESSION_NOT_FOUND;
        }

        return result;
    }

    private void doGetVotesInSession(final SessionDTO session, final Map<String, String> votes) {
        votes.clear();
        session.getVotes().entrySet().stream().forEach(e -> {
            String key = session.isPlayersVisible() ? e.getKey().getName() : "##HIDE##";
            String value = session.isVotingOpen() ? "##HIDE##" : e.getValue();
            votes.put(key, value);
        });
    }

    @Override
    public SessionDTO createSession(final SessionDTO message, final String userSessionId) {
        message.setId(getUniqueId(SESSIONS));
        PlayerDTO owner = findPlayer(userSessionId).get();
        message.setOwner(owner);
        message.getPlayers().add(owner);
        SESSIONS.add(message);

        log.info(">>> created session via WS: {}", message);

        return message;
    }

    @Override
    public MessageType joinSession(final SessionDTO message, final String userSessionId) {

        Optional<PlayerDTO>  playerSearch  = findPlayer(userSessionId);
        Optional<SessionDTO> sessionSearch = findSession(message.getId());

        MessageType result = null;

        if (playerSearch.isEmpty()) {
            result = MessageType.FAIL_JOIN_SESSION_PLAYER_NOT_FOUND;
        } else if (sessionSearch.isEmpty()) {
            result = MessageType.FAIL_JOIN_SESSION_SESSION_NOT_FOUND;
        } else if (StringUtils.hasLength(sessionSearch.get().getPassword()) && ! Objects.equals(sessionSearch.get().getPassword(), message.getPassword())) {
            result = MessageType.FAIL_JOIN_SESSION_AUTH_FAIL;
        } else {
            PlayerDTO  player  = playerSearch.get();
            SessionDTO session = sessionSearch.get();
            if (! session.getPlayers().contains(player)) {
                session.getPlayers().add(player);
                log.info(">>> player {} joined session via WS: {}", player, session);
            } else {
                log.info(">>> player {} had already joined session via WS: {}", player, session);
            }
            message.setOwner(session.getOwner());
            message.setVotingOpen(session.isVotingOpen());
            result = MessageType.JOINED_SESSION;
        }

        return result;
    }

    private <T extends UniqueIdentifiable> boolean exists(final String id, final Collection<T> existing) {
        return existing.stream().anyMatch(i -> i.getId().equals(id));
    }
}
