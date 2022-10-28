package src.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import src.application.controller.request.UserRequest;
import src.domain.service.UserRegistryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRegistryService service;

    @PostMapping
    public ResponseEntity<Void> registry(
            @RequestBody @Valid UserRequest request,
            UriComponentsBuilder uriBuilder
    ) {

        logger.info("USER CONTROLLER - REGISTRY - User: {}", request.getName());

        var userId = service.registry(request.toDomain());

        var uri = uriBuilder
                .path("/users/{userId}")
                .buildAndExpand(userId)
                .toUri();

        logger.info("USER CONTROLLER - REGISTRY - User: {}", request.getName());

        return ResponseEntity.created(uri).build();

    }

}
