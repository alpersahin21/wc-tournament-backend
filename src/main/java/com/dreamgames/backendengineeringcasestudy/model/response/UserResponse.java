package com.dreamgames.backendengineeringcasestudy.model.response;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;

    private Integer level;

    private String username;

    private Integer coins;

    private Country country;

    public static UserResponse fromModel(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .level(user.getLevel())
                .username(user.getUsername())
                .coins(user.getCoins())
                .country(user.getCountry())
                .build();
    }
}
