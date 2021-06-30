package com.alexmegremis.planningpokerapi;

import com.alexmegremis.planningpokerapi.api.model.*;

import java.util.Collection;

public interface GameService {
    <T extends UniqueIdentifiable> String getUniqueId(final Collection<T> existing);
    SessionDTO createSession(final SessionDTO message, final String sessionId);
    PlayerDTO createPlayer (final PlayerDTO message, final String sessionId);
}
