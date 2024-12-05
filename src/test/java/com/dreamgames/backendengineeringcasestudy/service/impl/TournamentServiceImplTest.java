package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.*;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentServiceImplTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentGroupRepository tournamentGroupRepository;

    @Mock
    private TournamentParticipationRepository tournamentParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    private User user;
    private Tournament tournament;
    private TournamentGroup tournamentGroup;
    private TournamentParticipation tournamentParticipation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize mock data
        user = new User();
        user.setId("user123");
        user.setLevel(20);
        user.setCoins(2000);
        user.setCountry(Country.US);
        user.setTournamentParticipation(new ArrayList<>());

        tournament = new Tournament();
        tournament.setId("tournament123");
        tournament.setActive(true);
        tournament.setTournamentGroups(new ArrayList<>());

        tournamentGroup = new TournamentGroup();
        tournamentGroup.setId("group123");
        tournamentGroup.setParticipantUsers(new ArrayList<>());
        tournament.getTournamentGroups().add(tournamentGroup);

        tournamentParticipation = new TournamentParticipation();
        tournamentParticipation.setUser(user);
        tournamentParticipation.setTournamentGroup(tournamentGroup);
        tournamentParticipation.setScore(0);
        tournamentParticipation.setRewardClaimed(false);
        user.getTournamentParticipation().add(tournamentParticipation);
    }

    @Test
    void testAddUserToTournament_Success() {
        // Arrange
        user.getTournamentParticipation().forEach(participation -> participation.setRewardClaimed(true));

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(tournamentRepository.findByActiveTrue()).thenReturn(Optional.of(tournament));
        when(tournamentGroupRepository.save(any(TournamentGroup.class))).thenReturn(tournamentGroup);

        // Act
        GroupLeaderboardResponse response = tournamentService.addUserToTournament("user123");

        // Assert
        assertNotNull(response);
        assertEquals(1, tournamentGroup.getParticipantUsers().size());
        verify(userRepository).findById("user123");
        verify(tournamentRepository).findByActiveTrue();
        verify(tournamentGroupRepository).save(any(TournamentGroup.class));
    }


    @Test
    void testAddUserToTournament_UserNotEligible() {
        // Arrange
        user.getTournamentParticipation().clear();
        user.setLevel(10); // User below required level
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        // Act & Assert
        ApiBusinessException exception = assertThrows(ApiBusinessException.class, () -> tournamentService.addUserToTournament("user123"));
        assertEquals("User does not meet the level requirement to participate in the tournament.", exception.getMessage());
    }

    @Test
    void testHandleClaimReward_Success() {
        // Arrange
        tournament.setActive(false);
        tournamentGroup.setTournament(tournament); // Link the tournament to the group
        tournamentParticipation.setTournamentGroup(tournamentGroup); // Link the group to the participation

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = tournamentService.handleClaimReward("user123");

        // Assert
        assertNotNull(response);
        assertTrue(tournamentParticipation.getRewardClaimed(), "Reward should be marked as claimed.");
        verify(userRepository).findById("user123");
        verify(userRepository).save(user);
    }


    @Test
    void testHandleClaimReward_TournamentActive() {
        // Arrange
        Tournament tournament = new Tournament();
        tournament.setActive(true);

        tournamentGroup.setTournament(tournament); // Link the tournament to the group
        tournamentParticipation.setTournamentGroup(tournamentGroup); // Link the group to the participation

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        // Act & Assert
        ApiBusinessException exception = assertThrows(ApiBusinessException.class, () -> tournamentService.handleClaimReward("user123"));
        assertEquals("Tournament is still active.", exception.getMessage());
    }


    @Test
    void testGetUserGroupRank() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        // Act
        GroupRankResponse response = tournamentService.getUserGroupRank("user123");

        // Assert
        assertNotNull(response);
        assertEquals("user123", response.getUserId());
        verify(userRepository).findById("user123");
    }

    @Test
    void testGetCountryLeaderboard() {
        // Arrange
        TournamentGroup group = new TournamentGroup();
        group.setParticipantUsers(new ArrayList<>());

        TournamentParticipation participation = new TournamentParticipation();
        participation.setScore(100);
        participation.setUser(user);

        group.getParticipantUsers().add(participation);

        tournament.setTournamentGroups(Collections.singletonList(group));

        when(tournamentRepository.findByActiveTrue()).thenReturn(Optional.of(tournament));

        // Act
        CountryLeaderboardResponse response = tournamentService.getCountryLeaderboard();

        // Assert
        assertNotNull(response, "Response should not be null.");
        assertFalse(response.getCountryRanks().isEmpty(), "Country ranks should not be empty.");
        assertTrue(response.getCountryRanks().stream()
                        .anyMatch(map -> map.containsKey(Country.US) && map.get(Country.US) == 100),
                "Country leaderboard should contain the correct score for USA.");
        verify(tournamentRepository).findByActiveTrue();
    }


    @Test
    void testUpdateUserScore() {
        // Arrange
        tournamentGroup.setCompeting(true); // Ensure the group is marked as competing
        tournamentParticipation.setTournamentGroup(tournamentGroup); // Link the participation to the group
        tournamentParticipation.setScore(0); // Initialize score to 0
        user.getTournamentParticipation().add(tournamentParticipation); // Link participation to user

        when(tournamentParticipationRepository.save(any(TournamentParticipation.class))).thenReturn(tournamentParticipation);

        // Act
        tournamentService.updateUserScore(user);

        // Assert
        assertEquals(1, tournamentParticipation.getScore(), "Score should be incremented by 1.");
        verify(tournamentParticipationRepository).save(tournamentParticipation);
    }


    @Test
    void testCreateNewTournament() {
        // Act
        tournamentService.createNewTournament();

        // Assert
        verify(tournamentRepository).findByActiveTrue();
        verify(tournamentRepository).save(any(Tournament.class));
    }
}
