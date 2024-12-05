package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TournamentControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TournamentController tournamentController;

    @Mock
    private TournamentService tournamentService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testEnterTournament() throws Exception {
        String userId = "123";
        GroupRankResponse groupRank = GroupRankResponse.builder()
                .userId("123")
                .username("John Doe")
                .country(Country.US)
                .score(1500)
                .rank(1)
                .build();

        GroupLeaderboardResponse response = GroupLeaderboardResponse.builder()
                .groupRankResponses(Collections.singletonList(groupRank))
                .build();

        when(tournamentService.addUserToTournament(anyString())).thenReturn(response);

        mockMvc.perform(post("/tournament/enter/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupRankResponses[0].userId").value("123"))
                .andExpect(jsonPath("$.groupRankResponses[0].username").value("John Doe"));
    }

    @Test
    void testClaimTournamentRewards() throws Exception {
        String userId = "123";
        UserResponse response = UserResponse.builder()
                .id("123")
                .username("John Doe")
                .level(5)
                .coins(1000)
                .country(Country.US)
                .build();

        when(tournamentService.handleClaimReward(anyString())).thenReturn(response);

        mockMvc.perform(post("/tournament/claim/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.username").value("John Doe"));
    }

    @Test
    void testGetGroupRank() throws Exception {
        String userId = "123";
        GroupRankResponse response = GroupRankResponse.builder()
                .userId("123")
                .username("John Doe")
                .country(Country.US)
                .score(1500)
                .rank(1)
                .build();

        when(tournamentService.getUserGroupRank(anyString())).thenReturn(response);

        mockMvc.perform(get("/tournament/rank/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("123"))
                .andExpect(jsonPath("$.rank").value(1));
    }

    @Test
    void testGetGroupLeaderboard() throws Exception {
        String groupId = "group123";
        GroupRankResponse groupRank = GroupRankResponse.builder()
                .userId("123")
                .username("John Doe")
                .country(Country.US)
                .score(1500)
                .rank(1)
                .build();

        GroupLeaderboardResponse response = GroupLeaderboardResponse.builder()
                .groupRankResponses(Collections.singletonList(groupRank))
                .build();

        when(tournamentService.getGroupLeaderboard(anyString())).thenReturn(response);

        mockMvc.perform(get("/tournament/leaderboard/group/{groupId}", groupId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupRankResponses[0].userId").value("123"))
                .andExpect(jsonPath("$.groupRankResponses[0].username").value("John Doe"));
    }

    @Test
    void testGetCountryLeaderboard() throws Exception {
        // Mock response
        CountryLeaderboardResponse response = CountryLeaderboardResponse.builder()
                .countryRanks(Collections.singletonList(Map.of(Country.TURKEY, 1)))
                .build();

        when(tournamentService.getCountryLeaderboard()).thenReturn(response);

        // Perform and assert
        mockMvc.perform(get("/tournament/leaderboard/country")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryRanks[0].Turkey").value(1)); // Adjusted path
    }

}
