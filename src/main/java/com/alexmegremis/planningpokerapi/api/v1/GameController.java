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

    @MessageMapping("game.vote.{gameSessionID}")
    @SendToUser ("/queue/reply")
    public MessageDTO<VoteDTO> voteInSession(@Payload final VoteDTO message,
                                             @DestinationVariable final String gameSessionID,
                                             final SimpMessageHeaderAccessor headerAccessor) {
        MessageType result = this.gameService.vote(message, headerAccessor.getSessionId());
        return MessageDTO.<VoteDTO>builder().messageType(result).payload(message).build();
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
