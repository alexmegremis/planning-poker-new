package com.alexmegremis.planningpokerapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
//@Component
public class PresenceEventListener {

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectedEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final String username = headerAccessor.getSessionAttributes().get("username").toString();
        log.info(">>> CONNECTED: {}", event.getUser());
        sendingOperations.convertAndSend("/topic/presence", ">>> CONNECTED <<< " + username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        final Object username = headerAccessor.getSessionAttributes().get("username");
        log.info(">>> DISCONNECTED: {}", username);
        sendingOperations.convertAndSend("/topic/presence", ">>> DISCONNECTED <<< " + username);
    }
}
