package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentParticipationRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final UserService userService;

    /**
     * Automatically creates a new tournament daily at 00:00 UTC
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    @Transactional
    @Override
    public void createNewTournament() {
        // Mark the previous tournament as inactive
        deactivatePreviousTournament();

        // Create a new tournament
        Tournament newTournament = new Tournament();
        newTournament.setStartTime(LocalDateTime.now());
        newTournament.setEndTime(LocalDateTime.now().plusHours(20)); // Tournament runs for 20 hours
        newTournament.setActive(true);

        // Save the new tournament to the database
        tournamentRepository.save(newTournament);
    }

    @Override
    public GroupLeaderboardResponse addUserToTournament(String userId) {
        return null;
    }

    @Override
    public UserResponse handleClaimReward(String userId) {
        return null;
    }

    @Override
    public GroupRankResponse getUserGroupRank(String userId) {
        return null;
    }

    @Override
    public GroupLeaderboardResponse getGroupLeaderboard(String tournamentGroupId) {
        return null;
    }

    @Override
    public CountryLeaderboardResponse getCountryLeaderboard() {
        return null;
    }

    /**
     * Deactivates the currently active tournament.
     */
    private void deactivatePreviousTournament() {
        // Find the active tournament
        Tournament activeTournament = getCurrentTournament();
        activeTournament.setActive(false);
        tournamentRepository.save(activeTournament);
    }

    private Tournament getCurrentTournament() {
        return tournamentRepository.findByActiveTrue().orElseThrow(() -> new ApiBusinessException("No active tournament found"));
    }


}
