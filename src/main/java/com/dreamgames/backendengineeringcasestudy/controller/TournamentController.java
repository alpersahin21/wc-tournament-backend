package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping("/enter/{userId}")
    public ResponseEntity<GroupLeaderboardResponse> enterTournament(@PathVariable String userId) {
        return ResponseEntity.ok(tournamentService.addUserToTournament(userId));
    }

    @PostMapping("/claim/{userId}")
    public ResponseEntity<UserResponse> claimTournamentRewards(@PathVariable String userId) {
        return ResponseEntity.ok(tournamentService.handleClaimReward(userId));
    }

    @GetMapping("/rank/{userId}")
    public ResponseEntity<GroupRankResponse> getGroupRank(@PathVariable String userId) {
        return ResponseEntity.ok(tournamentService.getUserGroupRank(userId));
    }

    @GetMapping("/leaderboard/group/{groupId}")
    public ResponseEntity<GroupLeaderboardResponse> getGroupLeaderboard(@PathVariable String groupId) {
        return ResponseEntity.ok(tournamentService.getGroupLeaderboard(groupId));
    }

    @GetMapping("/leaderboard/country")
    public ResponseEntity<CountryLeaderboardResponse> getCountryLeaderboard() {
        return ResponseEntity.ok(tournamentService.getCountryLeaderboard());
    }
}

