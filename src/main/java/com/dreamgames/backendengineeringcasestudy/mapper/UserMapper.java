package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.enums.Country;

public class UserMapper {

    private UserMapper() {
    }

    public static void fillUserData(User user) {
        user.setCoins(5000);
        user.setCountry(Country.getRandomCountry());
        user.setLevel(1);
    }
}
