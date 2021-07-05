package com.alexmegremis.planningpokerapi.api.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.*;

@ToString
@Getter
public class SessionUpdateDTO implements UniqueIdentifiable {

    private final String    id;
    private final String    name;
    private final PlayerDTO owner;
    private final Boolean   ownerCanVote;
    private final Boolean   playersVisible;
    private final Boolean   votingOpen;
    private final Long      version;

    @Builder
    public SessionUpdateDTO(final String id, final String name, final PlayerDTO owner,
                            final boolean ownerCanVote, final boolean votingOpen, final boolean playersVisible,
                            final long version, final ZonedDateTime created, final ZonedDateTime updated) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.ownerCanVote = ownerCanVote;
        this.votingOpen = votingOpen;
        this.playersVisible = playersVisible;
        this.version = version;
        this.created = created;
        this.updated = updated;
    }

    public static SessionUpdateDTO create(final SessionDTO session) {
        SessionUpdateDTO result = SessionUpdateDTO.builder()
                                                  .id(session.getId())
                                                  .name(session.getName())
                                                  .owner(session.getOwner())
                                                  .ownerCanVote(session.getOwnerCanVote())
                                                  .votingOpen(session.getVotingOpen())
                                                  .playersVisible(session.getPlayersVisible())
                                                  .version(session.getVersion())
                                                  .created(session.getCreated())
                                                  .updated(session.getUpdated())
                                                  .build();
        doGetVotesInSession(session, result);
        return result;
    }

    private static void doGetVotesInSession(final SessionDTO session, final SessionUpdateDTO sessionUpdate) {
        sessionUpdate.votes.clear();
        session.getVotes().entrySet().stream().forEach(e -> {
            String key = session.getPlayersVisible() ? e.getKey().getName() : "##HIDE##";
            String value = session.getVotingOpen() ? "##HIDE##" : e.getValue();
            sessionUpdate.votes.add(new String[]{key, value});
        });
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ZonedDateTime created;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final ZonedDateTime updated;
    @ToString.Exclude
    private final List<String[]> votes = new LinkedList<>();
}
