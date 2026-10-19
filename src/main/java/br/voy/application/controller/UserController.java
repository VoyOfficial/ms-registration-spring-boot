package br.voy.application.controller;

import br.voy.application.controller.openapi.CreatedNoContentApiResponses;
import br.voy.application.controller.openapi.GetUserApiResponses;
import br.voy.application.controller.request.UserRequest;
import br.voy.application.controller.response.UserResponse;
import br.voy.domain.usecase.GetUserUseCase;
import br.voy.domain.usecase.UserRegistryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "User", description = "Endpoint with all operations of User")
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRegistryUseCase userRegistryService;
    private final GetUserUseCase getUserService;

    public UserController(UserRegistryUseCase userRegistryService, GetUserUseCase getUserService) {
        this.userRegistryService = userRegistryService;
        this.getUserService = getUserService;
    }

    @Operation(summary = "Register a new User")
    @CreatedNoContentApiResponses
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> registry(
            @Parameter(description = "Payload used to create a user", required = true)
                    @RequestBody
                    @Valid
                    UserRequest request,
            UriComponentsBuilder uriBuilder) {

        logger.info("USER CONTROLLER - REGISTRY - User: {}", request.getName());

        var userId = userRegistryService.registry(request.toDomain());

        var uri = uriBuilder.path("/v1/users/{userId}").buildAndExpand(userId).toUri();

        logger.info("USER CONTROLLER - REGISTERED USER - User: {}", userId);

        return ResponseEntity.created(uri).build();
    }

    @Operation(summary = "Get an User by ID")
    @GetUserApiResponses
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "Numeric user identifier", required = true)
                    @Schema(example = "1", type = "Long")
                    @PathVariable
                    Long userId) {

        logger.info("USER CONTROLLER - GET USER BY ID - UserID: {}", userId);

        var userDomain = getUserService.getUserById(userId);

        logger.info("USER CONTROLLER - USER FOUND - User: {}", userDomain);

        var response = new UserResponse(userDomain);

        return ResponseEntity.ok(response);
    }
}
