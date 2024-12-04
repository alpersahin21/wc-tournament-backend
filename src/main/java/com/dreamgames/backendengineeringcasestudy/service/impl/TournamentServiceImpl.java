package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
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

    @Override
    public Tournament getCurrentTournament() {
        return tournamentRepository.findByIsActiveTrue().orElseThrow(() -> new ApiBusinessException("No active tournament found"));
    }

    @Override
    public TournamentGroup addUserToTournamentGroup(String userId, String tournamentGroupId) {
        return null;
    }

    @Override
    public TournamentGroup getTournamentGroupRankByUserId(String tournamentGroupId) {
        return null;
    }

    @Override
    public TournamentGroup getTournamentGroupLeaderboard(String tournamentGroupId) {
        return null;
    }

    @Override
    public TournamentGroup getCountryTournamentRank(String tournamentGroupId, String country) {
        return null;
    }

    /**
     * Automatically creates a new tournament daily at 00:00 UTC
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    @Transactional
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

    /**
     * Deactivates the currently active tournament.
     */
    public void deactivatePreviousTournament() {
        // Find the active tournament
        Tournament activeTournament = getCurrentTournament();
        activeTournament.setActive(false);
        tournamentRepository.save(activeTournament);
    }
}
