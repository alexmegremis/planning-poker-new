package com.alexmegremis.planningpokerapi.api.model;

import lombok.*;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
@Getter
public class SessionUpdateDTO implements UniqueIdentifiable {

    private final String    id;
    private final String    name;
    private final PlayerDTO owner;
    private final Boolean   ownerCanVote;
    private final Boolean   playersVisible;
    private final Boolean   votesVisible;
    private final Boolean   votesAlwaysVisible;
    private final Boolean   votingOpen;
    private final String    voteResult;
    private final Long      version;

    @Builder
    public SessionUpdateDTO(final String id, final String name, final PlayerDTO owner,
                            final boolean ownerCanVote, final boolean votingOpen, final boolean playersVisible, final boolean votesVisible, final boolean votesAlwaysVisible,
                            final String voteResult, final long version, final ZonedDateTime created, final ZonedDateTime updated) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.ownerCanVote = ownerCanVote;
        this.votingOpen = votingOpen;
        this.playersVisible = playersVisible;
        this.votesVisible = votesVisible;
        this.votesAlwaysVisible = votesAlwaysVisible;
        this.voteResult = voteResult;
        this.version = version;
        this.created = created;
        this.updated = updated;
    }

    public static SessionUpdateDTO create(final SessionDTO session) {
        String voteResult = null;
        if(session.getVotesVisible()) {
            voteResult = getVoteResult(session);
        } else {
            voteResult = "N/A";
        }
        SessionUpdateDTO result = SessionUpdateDTO.builder()
                                                  .id(session.getId())
                                                  .name(session.getName())
                                                  .owner(session.getOwner())
                                                  .ownerCanVote(session.getOwnerCanVote())
                                                  .votingOpen(session.getVotingOpen())
                                                  .votesVisible(session.getVotesVisible())
                                                  .votesAlwaysVisible(session.getVotesAlwaysVisible())
                                                  .playersVisible(session.getPlayersVisible())
                                                  .voteResult(voteResult)
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
            String value = (!session.getVotesVisible()) ? "##HIDE##" : e.getValue();
            sessionUpdate.votes.add(new String[]{key, value});
        });
    }

    private static String getVoteResult(final SessionDTO session) {

        String result = null;
        Map<String, Long> collect = session.getVotes()
                                           .values()
                                           .stream()
                                           .filter(StringUtils :: hasText)
                                           .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Optional<Long> max = collect.values().stream().max(Comparator.naturalOrder());

        if(max.isPresent()){
            result = collect.entrySet().stream().filter(e -> e.getValue().equals(max.get())).map(Map.Entry :: getKey).map(String :: valueOf).collect(Collectors.joining(","));
            result = result + " with " + max.get() + " votes";
        }

        return result;
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
