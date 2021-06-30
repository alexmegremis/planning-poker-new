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
public class PlayerController {

    private final GameService gameService;

    public PlayerController(final GameService gameService) {this.gameService = gameService;}

    @MessageMapping ("game.newPlayer")
    @SendToUser ("/queue/reply")
    public MessageDTO<PlayerDTO> createPlayer(@Payload final PlayerDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        PlayerDTO result = gameService.createPlayer(dto, headerAccessor.getSessionId());
        return MessageDTO.CREATED(result, MessageType.CREATED_PLAYER);
    }
}
