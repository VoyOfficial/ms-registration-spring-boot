package src.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.application.controller.request.UserRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    public ResponseEntity registry(@RequestBody @Valid UserRequest request) {

        logger.info("USER CONTROLLER - REGISTRY - User: {}", request.getName());

        return ResponseEntity.ok().body(request);
    }

}
