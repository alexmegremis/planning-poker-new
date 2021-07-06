package com.alexmegremis.planningpokerapi.api.v1;

import com.alexmegremis.planningpokerapi.GameService;
import com.alexmegremis.planningpokerapi.api.model.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@Slf4j
public class GameController {

    private Gson gson = new Gson();

    private final SimpMessagingTemplate messagingTemplate;

    private final GameService gameService;

    public GameController(final SimpMessagingTemplate messagingTemplate, final GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    // This is likely unnecessary
    @MessageMapping("game.vote.{gameSessionID}")
    public void voteInSession(@Payload final VoteDTO message,
                                             @DestinationVariable final String gameSessionID,
                                             final SimpMessageHeaderAccessor headerAccessor) {
        broadcastVotesInSession(gameSessionID);
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

    @SubscribeMapping("/game/foobartest")
    public void handleTestSubscribeEvent() {
        log.info(">>> Handling GAME/FOOBARTEST subscribe");
    }

    @SubscribeMapping("/app/game/foobartest")
    public void handleTestSubscribeEvent02() {
        log.info(">>> Handling APP/GAME/FOOBARTEST subscribe");
    }

    @SubscribeMapping("/foobartest")
    public void handleTestSubscribeEvent04() {
        log.info(">>> Handling /FOOBARTEST subscribe");
    }

    @SubscribeMapping("public")
    public void handleGreetingsPublicSubscribeEvent() {
        log.info(">>> Handling Public subscribe");
    }

    public void broadcastVotesInSession(final String gameSessionID) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.getSessionUpdate(gameSessionID);
        log.info(">>> Broadcasting session {} update", gameSessionID);
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.openVoting.{gameSessionID}")
    public void openVoting(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageType result = this.gameService.openVoting(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, MessageDTO.builder().messageType(result).build());
    }

    @MessageMapping("game.closeVoting.{gameSessionID}")
    public void closeVoting(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageType result = this.gameService.closeVoting(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, MessageDTO.builder().messageType(result).build());
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

    @MessageMapping ("/message")
    @SendToUser ("/queue/reply")
    public String processMessageFromClient(@Payload String message, Principal principal) throws Exception {
        return gson.fromJson(message, Map.class).get("name").toString();
    }

    @MessageExceptionHandler
    @SendToUser ("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }


}
