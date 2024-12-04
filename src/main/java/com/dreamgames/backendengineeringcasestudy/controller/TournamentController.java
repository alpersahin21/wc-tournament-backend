package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping("/enter/{userId}")
    public GroupLeaderboardResponse enterTournament(@PathVariable String userId) {
        return tournamentService.addUserToTournament(userId);
    }

    @PostMapping("/claim/{userId}")
    public UserResponse claimTournamentRewards(@PathVariable String userId) {
        return tournamentService.handleClaimReward(userId);
    }

    @GetMapping("/rank/{userId}")
    public GroupRankResponse getGroupRank(@PathVariable String userId) {
        return tournamentService.getUserGroupRank(userId);
    }

    @GetMapping("/leaderboard/group/{groupId}")
    public GroupLeaderboardResponse getGroupLeaderboard(@PathVariable String groupId) {
        return tournamentService.getGroupLeaderboard(groupId);
    }

    @GetMapping("/leaderboard/country")
    public CountryLeaderboardResponse getCountryLeaderboard() {
        return tournamentService.getCountryLeaderboard();
    }

}
