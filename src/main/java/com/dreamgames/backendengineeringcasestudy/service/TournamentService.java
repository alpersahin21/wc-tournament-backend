package com.dreamgames.backendengineeringcasestudy.service;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;

public interface TournamentService {

    Tournament getCurrentTournament();

    TournamentGroup addUserToTournamentGroup(String userId, String tournamentGroupId);

    TournamentGroup getTournamentGroupRankByUserId(String tournamentGroupId);

    TournamentGroup getTournamentGroupLeaderboard(String tournamentGroupId);

    TournamentGroup getCountryTournamentRank(String tournamentGroupId, String country);




}
