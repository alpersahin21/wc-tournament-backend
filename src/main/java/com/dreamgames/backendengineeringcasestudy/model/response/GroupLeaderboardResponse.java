package com.dreamgames.backendengineeringcasestudy.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupLeaderboardResponse {

    private List<GroupRankResponse> groupRankResponses;
}
