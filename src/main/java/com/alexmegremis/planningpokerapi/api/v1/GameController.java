package com.alexmegremis.planningpokerapi.api.v1;

import com.alexmegremis.planningpokerapi.GameService;
import com.alexmegremis.planningpokerapi.api.model.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
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

    @MessageMapping ("/hello")
    @SendTo ("/topic/greetings")
    public String greeting(String name) {
        return "Hello " + name;
    }

    public void push(final GameStateDTO update) {
        this.messagingTemplate.convertAndSend("/topic/game/", ">>> PING!!");
    }

//    @Scheduled (fixedRate = 60000)
//    private void ping() {
//        log.info(">>> ping!");
//        this.messagingTemplate.convertAndSend("/topic/greetings", ">>> PING!!");
//        this.greeting("Ping");
//    }

    // This is likely unnecessary
    @MessageMapping("game.vote.{gameSessionID}")
//    @SendToUser ("/queue/reply")
//    public MessageDTO<VoteDTO> voteInSession(@Payload final VoteDTO message,
    public void voteInSession(@Payload final VoteDTO message,
                                             @DestinationVariable final String gameSessionID,
                                             final SimpMessageHeaderAccessor headerAccessor) {
        MessageType result = this.gameService.vote(message, headerAccessor.getSessionId());
        broadcastVotesInSession(gameSessionID);
//        return MessageDTO.<VoteDTO>builder().messageType(result).build();
    }

    @MessageMapping("game.{gameSessionID}")
    public void getSession(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.getSessionUpdate(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

//    class VotesDTO {
//        public Map<String, String> votes;
//        public VotesDTO(final Map<String, String> votes) {
//            this.votes = votes;
//        }
//    }
    public void broadcastVotesInSession(final String gameSessionID) {
        MessageDTO<SessionUpdateDTO> result = this.gameService.getSessionUpdate(gameSessionID);
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, result);
    }

    @MessageMapping("game.voteOpen.{gameSessionID}")
    public void openVoting(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageType result = this.gameService.openVoting(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, MessageDTO.builder().messageType(result).build());
    }

    @MessageMapping("game.voteClose.{gameSessionID}")
    public void closeVoting(@DestinationVariable final String gameSessionID, final SimpMessageHeaderAccessor headerAccessor) {
        MessageType result = this.gameService.closeVoting(gameSessionID, headerAccessor.getSessionId());
        this.messagingTemplate.convertAndSend("/topic/game/" + gameSessionID, MessageDTO.builder().messageType(result).build());
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
