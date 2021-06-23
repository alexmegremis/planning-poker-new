package com.alexmegremis.planningpokerapi.api.v1;

import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.alexmegremis.planningpokerapi.api.model.ResponseDTO.CREATED;
import static com.alexmegremis.planningpokerapi.api.model.ResponseDTO.OK;

@RestController
@RequestMapping ("/api/v1/player")
@Slf4j
public class PlayerController {

    private static final List<PlayerDTO> players = new CopyOnWriteArrayList<>();

    @GetMapping (path = "newID", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public ResponseDTO<String> getNewID() {
        String newId = getUniqueId(players);
        log.info(">>> new ID: {}", newId);
        return OK(newId);
    }

    @PostMapping (produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus (HttpStatus.CREATED)
    @ResponseBody
    @CrossOrigin
    public PlayerDTO createPlayer(final CreateDTO dto) {
        PlayerDTO newPlayer = PlayerDTO.builder().name(dto.getName()).id(getUniqueId(players)).build();
        log.info(">>> created player: {}", newPlayer);
        players.add(newPlayer);
//        return CREATED(newPlayer);
        return newPlayer;
    }

    public String getUniqueId(final Collection<PlayerDTO> existing) {

        String newId;

        do {
            newId = String.valueOf(Math.toIntExact(Math.round(Math.random() * ((9999999 - 1000000) + 1)) + 1000000));
        } while (exists(newId, existing));

        return newId;
    }

    public boolean exists(final String id, final Collection<PlayerDTO> existing) {
        return existing.stream().anyMatch(i -> i.getId().equals(id));
    }
}
