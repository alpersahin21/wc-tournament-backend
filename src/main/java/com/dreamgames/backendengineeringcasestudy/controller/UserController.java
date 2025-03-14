package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest requestDTO) {
        User user = userService.createUser(requestDTO);
        return ResponseEntity.ok(UserResponse.fromModel(user));
    }

    @PutMapping("/level/{id}")
    public ResponseEntity<UserResponse> updateLevel(@PathVariable("id") String userId) {
        User user = userService.updateLevel(userId);
        return ResponseEntity.ok(UserResponse.fromModel(user));
    }
}

