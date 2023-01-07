package src.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import src.application.controller.request.UserRequest;
import src.application.controller.response.UserResponse;
import src.application.interceptor.StandardError;
import src.domain.usecase.GetUserUseCase;
import src.domain.usecase.UserRegistryUseCase;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Tag(name = "User", description = "Endpoint with all operations of User")
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRegistryUseCase userRegistryService;

    @Autowired
    private GetUserUseCase getUserService;

    @Operation(summary = "Register a new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created", content = @Content, headers = @Header(name = "Location", description = "Url to access the created resource")),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @ResponseStatus(CREATED)
    @PostMapping
    public ResponseEntity<Void> registry(
            @RequestBody @Valid UserRequest request,
            UriComponentsBuilder uriBuilder
    ) {

        logger.info("USER CONTROLLER - REGISTRY - User: {}", request.getName());

        var userId = userRegistryService.registry(request.toDomain());

        var uri = uriBuilder
                .path("/v1/users/{userId}")
                .buildAndExpand(userId)
                .toUri();

        logger.info("USER CONTROLLER - REGISTERED USER - User: {}", userId);

        return ResponseEntity.created(uri).build();

    }

    @Operation(summary = "Get an User by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Found", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @ResponseStatus(OK)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Schema(example = "1", type = "Long")
            @PathVariable
            Long userId
    ) {

        logger.info("USER CONTROLLER - GET USER BY ID - UserID: {}", userId);

        var userDomain = getUserService.getUserById(userId);

        logger.info("USER CONTROLLER - USER FOUND - User: {}", userDomain);

        var response = new UserResponse(userDomain);

        return ResponseEntity.ok(response);

    }

}
