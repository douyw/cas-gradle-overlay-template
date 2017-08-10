package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.ticket.registry.HazelcastTicketRegistry;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Really simple controller to allow authentication.
 */
@RestController
@RequestMapping(value = "/status/cache")
public class HazelcastController {
    private static final Logger log = LoggerFactory.getLogger(HazelcastController.class);

    private HazelcastTicketRegistry hazelcastTicketRegistry;

    public HazelcastController(HazelcastTicketRegistry hazelcastTicketRegistry) {
        this.hazelcastTicketRegistry = hazelcastTicketRegistry;
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity clear() throws CredentialsException, HttpAction {
        log.warn("Clearing all ticket registries");
        return new ResponseEntity<>(hazelcastTicketRegistry.deleteAll(), HttpStatus.OK);
    }

}
