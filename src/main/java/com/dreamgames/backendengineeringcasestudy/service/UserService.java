package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.User;

public interface UserService {

    User createUser();

    User updateLevel(String id);

    User retrieveUserById(String id);

}
