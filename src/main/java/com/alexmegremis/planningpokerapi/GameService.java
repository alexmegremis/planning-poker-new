package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;

import java.util.Collection;

public interface GameService {
    <T extends UniqueIdentifiable> String getUniqueId(final Collection<T> existing);
    SessionDTO createSession(final SessionDTO message, final String userSessionId);
    MessageType joinSession(final SessionDTO message, final String userSessionId);
    PlayerDTO createPlayer (final PlayerDTO message, final String userSessionId);
    MessageType vote(final VoteDTO message, final String userSessionId);
    MessageType openVoting(final String gameSessionID, final String userSessionId);
    MessageType closeVoting(final String gameSessionID, final String userSessionId);
    MessageType resetVoting(final String gameSessionID, final String userSessionId);
}
