package com.alexmegremis.planningpokerapi;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app/");
        config.enableSimpleBroker("/topic/", "/queue/");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/planning-poker-ws").setHandshakeHandler(createHandshakeHandler()).setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/planning-poker-ws").setHandshakeHandler(createHandshakeHandler()).setAllowedOriginPatterns("*");
    }

    private DefaultHandshakeHandler createHandshakeHandler() {
        return new DefaultHandshakeHandler() {

            //Get sessionId from request and set it in Map attributes
            public boolean beforeHandshake(final ServerHttpRequest request, final ServerHttpResponse response, final WebSocketHandler wsHandler,
                                           final Map<String, String> attributes) throws Exception {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest
                            = (ServletServerHttpRequest) request;
                    HttpSession session = servletRequest
                                                  .getServletRequest().getSession();
                    attributes.put("sessionId", session.getId());
                }
                return true;
            }
        };
    }
}
