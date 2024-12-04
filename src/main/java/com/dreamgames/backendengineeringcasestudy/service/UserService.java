package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;

public interface UserService {

    User createUser(CreateUserRequest requestDTO);

    User updateLevel(String id);

    User retrieveUserById(String id);
}
