package com.alexmegremis.planningpokerapi.api.model;

public enum MessageType {
    CREATED_SESSION,
    JOINED_SESSION,
    FAIL_JOIN_SESSION_AUTH_FAIL,
    FAIL_JOIN_SESSION_NOT_FOUND,
    CREATED_PLAYER,
    VOTE_OPEN,
    VOTE_CLOSE,
    VOTE_ACK
}
