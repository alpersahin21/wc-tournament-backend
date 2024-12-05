package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipation;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TournamentMapperTest {

    private User user;
    private TournamentParticipation tournamentParticipation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("123");
        user.setUsername("JohnDoe");

        tournamentParticipation = new TournamentParticipation();
    }

    @Test
    void userToTournamentParticipation_shouldMapUserAndInitializeFields() {
        // Act
        TournamentMapper.userToTournamentParticipation(user, tournamentParticipation);

        // Assert
        assertEquals(user, tournamentParticipation.getUser(), "The user should be correctly mapped to the tournament participation.");
        assertEquals(0, tournamentParticipation.getScore(), "The initial score should be set to 0.");
        assertFalse(tournamentParticipation.getRewardClaimed(), "RewardClaimed should be initialized to false.");
    }

    @Test
    void userToTournamentParticipation_shouldHandleEmptyTournamentParticipation() {
        // Arrange
        TournamentParticipation emptyTournamentParticipation = new TournamentParticipation();

        // Act
        TournamentMapper.userToTournamentParticipation(user, emptyTournamentParticipation);

        // Assert
        assertEquals(user, emptyTournamentParticipation.getUser(), "The user should still be mapped correctly.");
        assertEquals(0, emptyTournamentParticipation.getScore(), "Score should be initialized to 0.");
        assertFalse(emptyTournamentParticipation.getRewardClaimed(), "RewardClaimed should be initialized to false.");
    }

    @Test
    void userToTournamentParticipation_shouldOverrideExistingFields() {
        // Arrange
        tournamentParticipation.setUser(new User());
        tournamentParticipation.setScore(100);
        tournamentParticipation.setRewardClaimed(true);

        // Act
        TournamentMapper.userToTournamentParticipation(user, tournamentParticipation);

        // Assert
        assertEquals(user, tournamentParticipation.getUser(), "The user should be updated in the tournament participation.");
        assertEquals(0, tournamentParticipation.getScore(), "The score should be reset to 0.");
        assertFalse(tournamentParticipation.getRewardClaimed(), "RewardClaimed should be reset to false.");
    }
}
