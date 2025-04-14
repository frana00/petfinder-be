package com.petsignal.user;

import com.petsignal.user.dto.UserRequest;
import com.petsignal.user.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {
    private final UserService userService;
    
    @Operation(operationId = "listUsers")
    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getAllUsers();
    }

    @Operation(operationId = "getUserById")
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Integer id) {
        return userService.findById(id);
    }
    
    @Operation(operationId = "createUser")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }
    
    @Operation(operationId = "updateUser")
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }
    
    @Operation(operationId = "deleteUser")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
} 