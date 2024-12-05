package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;

public interface TournamentService {

    void createNewTournament();

    GroupLeaderboardResponse addUserToTournament(String userId);

    UserResponse handleClaimReward(String userId);

    GroupRankResponse getUserGroupRank(String userId);

    GroupLeaderboardResponse getGroupLeaderboard(String tournamentGroupId);

    CountryLeaderboardResponse getCountryLeaderboard();

    void updateUserScore(User user);
}
