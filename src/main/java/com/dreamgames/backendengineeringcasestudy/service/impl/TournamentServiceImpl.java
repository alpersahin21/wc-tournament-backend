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
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private final UserService userService;


    @Override
    public GroupLeaderboardResponse addUserToTournament(String userId) {
        Tournament tournament = getCurrentTournamentOrCreateNew();
        User user = userService.retrieveUserById(userId);
        checkIfUserIsEligible(user);
        TournamentGroup foundGroup = findGroupForUserOrCreateNew(user, tournament);
        return generateGroupLeaderboard(foundGroup);
    }

    private TournamentGroup checkIfGroupParticipantsAreMatched(TournamentGroup foundGroup) {
        if (foundGroup.getParticipantUsers().size() == 5) {
            foundGroup.setCompeting(true);
            return tournamentGroupRepository.save(foundGroup);
        }
        return foundGroup;
    }

    private void checkIfUserIsEligible(User user) {
        if (user.getLevel() < 20) {
            throw new ApiBusinessException("User does not have the level requirement to participate in the tournament");
        }
        else if (user.getCoins() < 1000) {
            throw new ApiBusinessException("User does not have enough coins to participate in the tournament");
        }
        List<TournamentParticipation> userParticipation = user.getTournamentParticipation();
        if (!CollectionUtils.isEmpty(userParticipation)) {
            userParticipation.forEach(participation -> {
                if (Boolean.FALSE.equals(participation.getRewardClaimed()))
                    throw new ApiBusinessException("User haven't claimed the reward from previous tournament");
            });
        }
    }

    private GroupLeaderboardResponse generateGroupLeaderboard(TournamentGroup foundGroup) {
        List<GroupRankResponse> groupRankResponses = foundGroup.getParticipantUsers().stream()
                .sorted((pu1, pu2) -> pu2.getScore().compareTo(pu1.getScore()))
                .map(participantUser -> generateUserGroupRank(participantUser.getUser(), participantUser))
                .toList();
        return GroupLeaderboardResponse.builder()
                .groupRankResponses(groupRankResponses)
                .build();
    }

    private TournamentGroup findGroupForUserOrCreateNew(User user, Tournament tournament) {
        // Retrieve tournament groups
        List<TournamentGroup> tournamentGroups = tournament.getTournamentGroups();

        // Iterate through existing groups to find a suitable group
        for (TournamentGroup group : tournamentGroups) {
            // Check if this group has any user from the same country as the given user
            boolean hasSameCountryUser = group.getParticipantUsers().stream()
                    .anyMatch(participation -> participation.getUser().getCountry() == user.getCountry());

            // If no user from the same country is found, add the user to this group
            if (!hasSameCountryUser) {
                addUserToGroup(user, group);
                return checkIfGroupParticipantsAreMatched(group);
            }
        }

        // If no suitable group is found, create a new one
        return createNewTournamentGroup(user, tournament);
    }

    private TournamentGroup createNewTournamentGroup(User user, Tournament tournament) {
        // Create a new tournament group
        TournamentGroup newGroup = new TournamentGroup();
        newGroup.setTournament(tournament);
        newGroup.setParticipantUsers(new ArrayList<>());
        newGroup.setCompeting(false);

        // Add the new group to the tournament
        tournament.getTournamentGroups().add(newGroup);

        // Add the user to this new group
        addUserToGroup(user, newGroup);

        // Save the tournament group (and cascade to participation)
        tournamentRepository.save(tournament); // Assuming this cascades to groups and participations

        return newGroup;
    }

    private void addUserToGroup(User user, TournamentGroup group) {
        // Create a new tournament participation for the user
        TournamentParticipation participation = new TournamentParticipation();
        TournamentMapper.userToTournamentParticipation(user, participation);

        participation.setTournamentGroup(group);

        // Add the participation to the group
        group.getParticipantUsers().add(participation);

        // Add the participation to the user's participation list
        user.getTournamentParticipation().add(participation);
    }


    @Override
    public UserResponse handleClaimReward(String userId) {
        User user = userService.retrieveUserById(userId);
        TournamentParticipation participation = getActiveTournamentParticipation(user);
        participation.setRewardClaimed(true);
        Integer userRank = findUserRank(participation);
        User savedUser = userService.updateUserData(user, userRank);
        tournamentParticipationRepository.save(participation);
        return UserResponse.fromModel(savedUser);
    }

    @Override
    public GroupRankResponse getUserGroupRank(String userId) {
        User user = userService.retrieveUserById(userId);
        TournamentParticipation participation = getActiveTournamentParticipation(user);
        return generateUserGroupRank(user, participation);
    }

    private TournamentParticipation getActiveTournamentParticipation(User user) {
        return user.getTournamentParticipation().stream()
                .filter(participation -> Boolean.FALSE.equals(participation.getRewardClaimed()))
                .findFirst()
                .orElseThrow(() -> new ApiBusinessException("User is not participating in any active tournament"));
    }

    private GroupRankResponse generateUserGroupRank(User user, TournamentParticipation participation) {
        return GroupRankResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .rank(findUserRank(participation))
                .build();
    }

    private Integer findUserRank(TournamentParticipation participation) {
        // Get the tournament group of the user
        TournamentGroup group = participation.getTournamentGroup();

        // Get all participation in the group
        List<TournamentParticipation> allParticipation = group.getParticipantUsers();

        // Sort the participation by score in descending order
        List<TournamentParticipation> sortedParticipation = allParticipation.stream()
                .sorted((p1, p2) -> p2.getScore().compareTo(p1.getScore())) // Higher scores first
                .toList();

        // Find the rank of the given user in the sorted list
        for (int i = 0; i < sortedParticipation.size(); i++) {
            if (sortedParticipation.get(i).getUser().getId().equals(participation.getUser().getId())) {
                return i + 1; // Rank is 1-based
            }
        }

        return -1;
    }


    @Override
    public GroupLeaderboardResponse getGroupLeaderboard(String tournamentGroupId) {
        TournamentGroup group = tournamentGroupRepository.findById(tournamentGroupId)
                .orElseThrow(() -> new ApiBusinessException("Tournament group not found"));
        return generateGroupLeaderboard(group);
    }

    @Override
    public CountryLeaderboardResponse getCountryLeaderboard() {
        // Find the active tournament
        Tournament tournament = tournamentRepository.findByActiveTrue()
                .orElseThrow(() -> new ApiBusinessException("No active tournament found"));

        // Retrieve all tournament groups
        List<TournamentGroup> tournamentGroups = tournament.getTournamentGroups();

        // Map to store country scores
        Map<Country, Integer> countryScores = new HashMap<>();

        // Calculate total scores for each country
        for (TournamentGroup group : tournamentGroups) {
            for (TournamentParticipation participation : group.getParticipantUsers()) {
                Country userCountry = participation.getUser().getCountry();
                int userScore = participation.getScore();

                countryScores.put(userCountry, countryScores.getOrDefault(userCountry, 0) + userScore);
            }
        }

        // Sort countries by their scores in descending order
        List<Map<Country, Integer>> sortedCountryScores = countryScores.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sort by scores (highest first)
                .map(entry -> Map.of(entry.getKey(), entry.getValue())) // Convert to desired map structure
                .toList();

        // Build and return the response
        return CountryLeaderboardResponse.builder()
                .countryRanks(sortedCountryScores)
                .build();
    }

    @Override
    public void updateUserScore(User user) {
        TournamentParticipation participation = getActiveTournamentParticipation(user);
        if (Boolean.TRUE.equals(participation.getTournamentGroup().getCompeting()))
            participation.setScore(participation.getScore() + 1);
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
    @Transactional
    @Override
    public void createNewTournament() {
        // Check and deactivate any existing tournament
        deactivatePreviousTournament();

        // Create a new tournament
        Tournament newTournament = new Tournament();
        newTournament.setStartTime(LocalDateTime.now());
        newTournament.setEndTime(LocalDateTime.now().plusHours(20)); // Set deadline to 20:00 UTC
        newTournament.setActive(true);

        // Save the new tournament to the database
        tournamentRepository.save(newTournament);
    }

    private void deactivatePreviousTournament() {
        // Find the active tournament or create one if none exists
        Tournament activeTournament = getCurrentTournamentOrCreateNew();

        if (Boolean.TRUE.equals(activeTournament.getActive())) {
            activeTournament.setActive(false);
            tournamentRepository.save(activeTournament);
        }
    }

    private Tournament getCurrentTournamentOrCreateNew() {
        return tournamentRepository.findByActiveTrue().orElseGet(() -> {
            Tournament newTournament = new Tournament();
            newTournament.setStartTime(LocalDateTime.now());
            newTournament.setEndTime(getTodayAt20UTC());
            newTournament.setActive(true);
            return tournamentRepository.save(newTournament);
        });
    }

    private LocalDateTime getTodayAt20UTC() {
        // Get current date and set the time to 20:00 UTC
        return LocalDate.now().atTime(20, 0).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }



}
