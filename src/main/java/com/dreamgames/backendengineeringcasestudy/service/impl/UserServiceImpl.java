package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
import com.dreamgames.backendengineeringcasestudy.service.TournamentService;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import com.dreamgames.backendengineeringcasestudy.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TournamentService tournamentService;

    @Override
    public User createUser(CreateUserRequest requestDTO) {
        log.info("Creating user with username: {}", requestDTO.getUsername());
        User user = new User();
        UserMapper.dtoToUser(user, requestDTO);
        return userRepository.save(user);
    }

    @Override
    public User updateLevel(String id) {
        User user = retrieveUserById(id);
        user.setLevel(user.getLevel() + 1);
        user.setCoins(user.getCoins() + 25);
        tournamentService.updateUserScore(user);
        return userRepository.save(user);
    }

    @Override
    public User retrieveUserById(String id) {
        validateId(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiBusinessException("User not found with ID: " + id));
    }

    @Override
    public User updateUserData(User user, Integer userRank) {
        int rewardCoins = switch (userRank) {
            case 1 -> 10000;
            case 2 -> 5000;
            default -> 0;
        };
        user.setCoins(user.getCoins() + rewardCoins);
        return userRepository.save(user);
    }

    private void validateId(String id) {
        if (Boolean.TRUE.equals(StringUtils.isEmpty(id))) throw new ApiBusinessException("User ID is required.");
    }
}

