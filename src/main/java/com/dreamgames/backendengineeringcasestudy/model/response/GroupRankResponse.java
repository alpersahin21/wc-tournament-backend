package com.dreamgames.backendengineeringcasestudy.model.response;

import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupRankResponse {

    private String userId;

    private String username;

    private Country country;

    private Integer rank;
}
