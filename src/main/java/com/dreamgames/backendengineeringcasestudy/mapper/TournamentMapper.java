package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipation;
import com.dreamgames.backendengineeringcasestudy.entity.User;

public class TournamentMapper {

    private TournamentMapper() {
    }

    public static void userToTournamentParticipation(User user, TournamentParticipation tournamentParticipation) {
        tournamentParticipation.setUser(user);
        tournamentParticipation.setScore(0); // Initial score
        tournamentParticipation.setRewardClaimed(false);
    }
}
