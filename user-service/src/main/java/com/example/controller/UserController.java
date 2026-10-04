package com.example.controller;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;
import com.example.exception.ErrorResponse;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Пользователи", description = "CRUD-операции над пользователями")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает коллекцию пользователей. Каждый элемент содержит HATEOAS-ссылки "
                    + "для перехода к ресурсу и выполнения операций над ним."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                    content = @Content(mediaType = "application/hal+json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    })
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAll() {
        List<EntityModel<UserResponse>> users = userService.getAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel(),
                linkTo(methodOn(UserController.class).create(null)).withRel("create"));
    }

    @Operation(summary = "Получить пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный идентификатор",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long id) {
        return toModel(userService.getById(id));
    }

    @Operation(summary = "Создать пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        EntityModel<UserResponse> createdUser = toModel(userService.create(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(createdUser.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(createdUser);
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                    content = @Content(mediaType = "application/hal+json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных или некорректный идентификатор",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return toModel(userService.update(id, request));
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Некорректный идентификатор",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UserResponse> toModel(UserResponse user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"),
                linkTo(methodOn(UserController.class).update(user.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(user.id())).withRel("delete"));
    }
}