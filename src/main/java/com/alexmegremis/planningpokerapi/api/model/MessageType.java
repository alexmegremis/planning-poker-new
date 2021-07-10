package com.alexmegremis.planningpokerapi.api.model;

public enum MessageType {
    CREATED_SESSION,
    JOINED_SESSION,
    FAIL_JOIN_SESSION_AUTH_FAIL,
    FAIL_JOIN_SESSION_SESSION_NOT_FOUND,
    FAIL_JOIN_SESSION_PLAYER_NOT_FOUND,
    CREATED_PLAYER,
    FOUND_PLAYER,
    FAIL_RECONNECT_PLAYER_NOT_FOUND,
    FAIL_RECONNECT_SESSION_NOT_FOUND,
    FAIL_VOTE_PLAYER_NOT_FOUND,
    FAIL_VOTE_SESSION_NOT_FOUND,
    FAIL_VOTE_PLAYER_NOT_IN_SESSION,
    FAIL_VOTE_MANAGEMENT_NOT_OWNER,
    VOTE_OPEN,
    VOTE_CLOSE,
    VOTE_ACK,
    VOTE_UPDATE,
    OK
}
