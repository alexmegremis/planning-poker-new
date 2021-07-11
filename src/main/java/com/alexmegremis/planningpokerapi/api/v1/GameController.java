package com.alexmegremis.planningpokerapi.api.v1;

import com.alexmegremis.planningpokerapi.GameService;
import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;

    public GameController(final SimpMessagingTemplate messagingTemplate, final GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    // This is likely unnecessary
    @MessageMapping("game.vote.{gameSessionID}")
    @SendToUser ("/queue/reply")
    public MessageDTO<String> voteInSession(@Payload final VoteDTO message,
                                             @DestinationVariable final String gameSessionID,
                                             final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<String> result = this.gameService.vote(message, headerAccessor.getSessionId());
        broadcastVotesInSession(gameSessionID);
        return result;
    }

    @MessageMapping("game.{gameSessionID}")
    @SendToUser ("/queue/reply")
    public MessageDTO<SessionUpdateDTO> getSession(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.getSessionUpdate(gameSessionID, headerAccessor.getSessionId());
        log.info(">>> Responding with session {} update to {}", gameSessionID, headerAccessor.getSessionId());
        return result;
    }

    @SubscribeMapping("/game/{gameSessionID}")
    public void handleSubscribeEvent(@DestinationVariable final String gameSessionID) {
        log.info(">>> Handling subscribe to {}", gameSessionID);
        broadcastVotesInSession(gameSessionID);
    }

    public void broadcastVotesInSession(final String gameSessionID) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.getSessionUpdate(gameSessionID);
        log.info(">>> Broadcasting session {} update", gameSessionID);
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.openVoting.{gameSessionID}")
    public void openVoting(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.openVoting(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.closeVoting.{gameSessionID}")
    public void closeVoting(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.closeVoting(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.showVotes.{gameSessionID}")
    public void showVotes(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.showVotes(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.hideVotes.{gameSessionID}")
    public void hideVotes(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.hideVotes(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.showPlayers.{gameSessionID}")
    public void showPlayers(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.showPlayers(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.hidePlayers.{gameSessionID}")
    public void hidePlayers(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.hidePlayers(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.resetVotes.{gameSessionID}")
    public void resetVotes(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.resetVotes(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageExceptionHandler
    @SendToUser ("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }


}
