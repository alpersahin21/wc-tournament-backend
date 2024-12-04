package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;

public class UserMapper {

    private UserMapper() {
    }

    public static void dtoToUser(User user, CreateUserRequest requestDTO) {
        user.setUsername(requestDTO.getUsername());
        user.setCoins(5000);
        user.setCountry(Country.getRandomCountry());
        user.setLevel(1);
    }
}
