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
        PlayerDTO player = gameService.createPlayer(dto, headerAccessor.getSessionId());
        MessageDTO<PlayerDTO> result = MessageDTO.CREATED(player, MessageType.CREATED_PLAYER);
        result.setMessage(player.getToken());
        return result;
    }

    @MessageMapping ("game.reconnectPlayer")
    @SendToUser ("/queue/reply")
    public MessageDTO<PlayerDTO> reconnectPlayer(@Payload final PlayerDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        log.debug(">>> Received reconnectPlayer call with {} and {}", dto, headerAccessor.getSessionId());
        MessageDTO<PlayerDTO> result = gameService.reconnectPlayer(dto, headerAccessor.getSessionId());
        return result;
    }
}
