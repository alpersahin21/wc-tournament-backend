package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse createUser() {
        User user = userService.createUser();
        return UserResponse.fromModel(user);
    }

    @PutMapping("/level/{id}")
    public UserResponse updateLevel(@PathVariable("id") String userId) {
        User user = userService.updateLevel(userId);
        return UserResponse.fromModel(user);
    }
}
