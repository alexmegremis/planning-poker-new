package com.alexmegremis.planningpokerapi.api.v1;

import com.alexmegremis.planningpokerapi.GameService;
import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class SessionController {

    private final GameService    gameService;
    private final GameController gameController;

    public SessionController(final GameService gameService, final GameController gameController) {
        this.gameService = gameService;
        this.gameController = gameController;
    }

    @MessageMapping ("game.newSession")
    @SendToUser ("/queue/reply")
    public MessageDTO<SessionUpdateDTO> createSession(@Payload final SessionDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = gameService.createSession(dto, headerAccessor.getSessionId());
        gameController.broadcastVotesInSession(dto.getId());
        return result;
    }

    @MessageMapping ("game.joinSession")
    @SendToUser ("/queue/reply")
    public MessageDTO<SessionUpdateDTO> joinSession(@Payload final SessionDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        MessageDTO<SessionUpdateDTO> result = gameService.joinSession(dto, headerAccessor.getSessionId());
        gameController.broadcastVotesInSession(dto.getId());
        return result;
    }
}
