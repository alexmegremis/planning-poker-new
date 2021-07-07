package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;

import java.util.Collection;
import java.util.Map;

public interface GameService {
    <T extends UniqueIdentifiable> String getUniqueId(final Collection<T> existing);
    MessageDTO<SessionUpdateDTO> createSession(final SessionDTO message, final String userSessionId);
    MessageDTO<SessionUpdateDTO> joinSession(final SessionDTO message, final String userSessionId);
    PlayerDTO createPlayer (final PlayerDTO message, final String userSessionId);
    MessageType vote(final VoteDTO message, final String userSessionId);
    MessageDTO<SessionUpdateDTO> getSessionUpdate(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> getSessionUpdate(final String gameSessionID);
    MessageDTO<SessionUpdateDTO> openVoting(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> closeVoting(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> resetVoting(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> showVotes(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> hideVotes(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> showPlayers(final String gameSessionID, final String userSessionId);
    MessageDTO<SessionUpdateDTO> hidePlayers(final String gameSessionID, final String userSessionId);
}
