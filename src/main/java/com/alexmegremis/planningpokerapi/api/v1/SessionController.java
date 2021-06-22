package com.alexmegremis.planningpokerapi.api.v1;

import com.alexmegremis.planningpokerapi.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.alexmegremis.planningpokerapi.api.model.ResponseDTO.OK;

@RestController
@RequestMapping ("/api/v1/session")
@Slf4j
public class SessionController {

    private static final List<SessionDTO> sessions = new CopyOnWriteArrayList<>();

    @PostMapping (params = {"name", "password"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus (HttpStatus.CREATED)
    @ResponseBody
    @CrossOrigin
    public SessionDTO createSession(@RequestParam("name") final String name, @RequestParam("password") final String password) {
        SessionDTO newSession = SessionDTO.builder().name(name).password(password).id(getUniqueId(sessions)).build();
        log.info(">>> created session: {}", newSession);
        sessions.add(newSession);
//        return CREATED(newPlayer);
        return newSession;
    }

    public String getUniqueId(final Collection<SessionDTO> existing) {

        String newId;

        do {
            newId = String.valueOf(Math.toIntExact(Math.round(Math.random() * ((9999999 - 1000000) + 1)) + 1000000));
        } while (exists(newId, existing));

        return newId;
    }

    public boolean exists(final String id, final Collection<SessionDTO> existing) {
        return existing.stream().anyMatch(i -> i.getId().equals(id));
    }
}
