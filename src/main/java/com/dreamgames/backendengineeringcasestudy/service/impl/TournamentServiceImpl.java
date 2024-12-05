package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.Tournament;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentGroup;
import com.dreamgames.backendengineeringcasestudy.entity.TournamentParticipation;
import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.mapper.TournamentMapper;
import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import com.dreamgames.backendengineeringcasestudy.model.response.CountryLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupLeaderboardResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.GroupRankResponse;
import com.dreamgames.backendengineeringcasestudy.model.response.UserResponse;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentGroupRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentParticipationRepository;
import com.dreamgames.backendengineeringcasestudy.repository.TournamentRepository;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentGroupRepository tournamentGroupRepository;
    private final TournamentParticipationRepository tournamentParticipationRepository;
    private final UserRepository userRepository;

    @Override
    public GroupLeaderboardResponse addUserToTournament(String userId) {
        log.info("Adding user to tournament: userId={}", userId);
        Tournament tournament = getCurrentTournamentOrCreateNew();
        User user = retrieveUserById(userId);

        validateUserEligibility(user);

        TournamentGroup group = assignUserToGroup(user, tournament);
        return generateGroupLeaderboard(group);
    }

    private void validateUserEligibility(User user) {
        if (user.getLevel() < 20) {
            throw new ApiBusinessException("User does not meet the level requirement to participate in the tournament.");
        }
        if (user.getCoins() < 1000) {
            throw new ApiBusinessException("User does not have enough coins to participate in the tournament.");
        }
        if (user.getTournamentParticipation().stream().anyMatch(p -> !p.getRewardClaimed())) {
            throw new ApiBusinessException("User has unclaimed rewards from a previous tournament.");
        }
    }

    private TournamentGroup assignUserToGroup(User user, Tournament tournament) {
        return tournament.getTournamentGroups().stream()
                .filter(group -> group.getParticipantUsers().stream()
                        .noneMatch(part -> part.getUser().getCountry().equals(user.getCountry())))
                .findFirst()
                .map(group -> addUserToGroup(user, group))
                .orElseGet(() -> createNewTournamentGroup(user, tournament));
    }

    private TournamentGroup addUserToGroup(User user, TournamentGroup group) {
        TournamentParticipation participation = createParticipation(user, group);
        group.getParticipantUsers().add(participation);
        user.getTournamentParticipation().add(participation);

        if (group.getParticipantUsers().size() == 5) {
            group.setCompeting(true);
        }
        return tournamentGroupRepository.save(group);
    }

    private TournamentParticipation createParticipation(User user, TournamentGroup group) {
        TournamentParticipation participation = new TournamentParticipation();
        TournamentMapper.userToTournamentParticipation(user, participation);
        participation.setTournamentGroup(group);
        return participation;
    }

    private TournamentGroup createNewTournamentGroup(User user, Tournament tournament) {
        TournamentGroup group = new TournamentGroup();
        group.setTournament(tournament);
        group.setParticipantUsers(new ArrayList<>());
        tournament.getTournamentGroups().add(group);

        return addUserToGroup(user, group);
    }

    @Override
    public UserResponse handleClaimReward(String userId) {
        log.info("Handling reward claim for user: userId={}", userId);
        User user = retrieveUserById(userId);
        TournamentParticipation participation = getActiveParticipation(user);

        participation.setRewardClaimed(true);
        Integer userRank = calculateUserRank(participation);
        int rewardCoins = switch (userRank) {
            case 1 -> 10000;
            case 2 -> 5000;
            default -> 0;
        };
        user.setCoins(user.getCoins() + rewardCoins);
        User savedEntity = userRepository.save(user);
        return UserResponse.fromModel(savedEntity);
    }

    @Override
    public GroupRankResponse getUserGroupRank(String userId) {
        log.info("Getting group rank for user: userId={}", userId);
        User user = retrieveUserById(userId);
        TournamentParticipation participation = getActiveParticipation(user);

        return generateGroupRankResponse(user, participation);
    }

    @Override
    public GroupLeaderboardResponse getGroupLeaderboard(String groupId) {
        log.info("Fetching group leaderboard: groupId={}", groupId);
        TournamentGroup group = tournamentGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiBusinessException("Tournament group not found."));
        return generateGroupLeaderboard(group);
    }

    @Override
    public CountryLeaderboardResponse getCountryLeaderboard() {
        log.info("Fetching country leaderboard.");
        Tournament tournament = tournamentRepository.findByActiveTrue()
                .orElseThrow(() -> new ApiBusinessException("No active tournament found."));

        Map<Country, Integer> countryScores = calculateCountryScores(tournament.getTournamentGroups());
        return buildCountryLeaderboardResponse(countryScores);
    }

    private Map<Country, Integer> calculateCountryScores(List<TournamentGroup> groups) {
        Map<Country, Integer> countryScores = new HashMap<>();
        for (TournamentGroup group : groups) {
            for (TournamentParticipation participation : group.getParticipantUsers()) {
                countryScores.merge(
                        participation.getUser().getCountry(),
                        participation.getScore(),
                        Integer::sum
                );
            }
        }
        return countryScores;
    }

    private CountryLeaderboardResponse buildCountryLeaderboardResponse(Map<Country, Integer> countryScores) {
        List<Map<Country, Integer>> sortedCountryScores = countryScores.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(entry -> Map.of(entry.getKey(), entry.getValue()))
                .toList();
        return CountryLeaderboardResponse.builder()
                .countryRanks(sortedCountryScores)
                .build();
    }

    @Override
    public void updateUserScore(User user) {
        log.info("Updating score for user: userId={}", user.getId());
        TournamentParticipation participation = getActiveParticipation(user);

        if (Boolean.TRUE.equals(participation.getTournamentGroup().getCompeting())) {
            participation.setScore(participation.getScore() + 1);
            tournamentParticipationRepository.save(participation);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    @Transactional
    @Override
    public void createNewTournament() {
        log.info("Creating a new tournament.");
        deactivatePreviousTournament();

        Tournament newTournament = new Tournament();
        newTournament.setStartTime(LocalDateTime.now());
        newTournament.setEndTime(getTodayAt20UTC());
        newTournament.setActive(true);
        tournamentRepository.save(newTournament);
    }

    private void deactivatePreviousTournament() {
        tournamentRepository.findByActiveTrue()
                .ifPresent(tournament -> {
                    tournament.setActive(false);
                    tournamentRepository.save(tournament);
                });
    }

    private Tournament getCurrentTournamentOrCreateNew() {
        return tournamentRepository.findByActiveTrue()
                .orElseGet(() -> {
                    Tournament newTournament = new Tournament();
                    newTournament.setStartTime(LocalDateTime.now());
                    newTournament.setEndTime(getTodayAt20UTC());
                    newTournament.setActive(true);
                    return tournamentRepository.save(newTournament);
                });
    }

    private TournamentParticipation getActiveParticipation(User user) {
        return user.getTournamentParticipation().stream()
                .filter(part -> !part.getRewardClaimed())
                .findFirst()
                .orElseThrow(() -> new ApiBusinessException("User is not participating in any active tournament."));
    }

    private Integer calculateUserRank(TournamentParticipation participation) {
        List<TournamentParticipation> sortedParticipants = participation.getTournamentGroup()
                .getParticipantUsers().stream()
                .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                .toList();

        return sortedParticipants.indexOf(participation) + 1;
    }

    private GroupLeaderboardResponse generateGroupLeaderboard(TournamentGroup group) {
        List<GroupRankResponse> groupRanks = group.getParticipantUsers().stream()
                .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                .map(part -> generateGroupRankResponse(part.getUser(), part))
                .toList();

        return GroupLeaderboardResponse.builder()
                .groupRankResponses(groupRanks)
                .build();
    }

    private GroupRankResponse generateGroupRankResponse(User user, TournamentParticipation participation) {
        return GroupRankResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .rank(calculateUserRank(participation))
                .build();
    }

    private LocalDateTime getTodayAt20UTC() {
        return LocalDate.now().atTime(20, 0).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    private User retrieveUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiBusinessException("User not found with ID: " + id));
    }
}

