package com.dreamgames.backendengineeringcasestudy.model.response;

import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CountryLeaderboardResponse {

    List<Map<Country, Integer>> countryRanks;
}
