package com.dreamgames.backendengineeringcasestudy.service.impl;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.exception.ApiBusinessException;
import com.dreamgames.backendengineeringcasestudy.mapper.UserMapper;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.repository.UserRepository;
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

    @Override
    public User createUser(CreateUserRequest requestDTO) {
        log.info("UserService -> createUser started");
        User user = new User();
        UserMapper.dtoToUser(user, requestDTO);
        User savedEntity = userRepository.save(user);
        log.info("UserService -> createUser completed!");
        return savedEntity;
    }

    @Override
    public User updateLevel(String id) {
        log.info("UserService -> updateLevel started: userId={}", id);
        User user = retrieveUserById(id);
        user.setLevel(user.getLevel() + 1);
        user.setCoins(user.getCoins() + 25);
        User savedEntity = userRepository.save(user);
        log.info("UserService -> updateLevel completed!");
        return savedEntity;
    }

    @Override
    public User retrieveUserById(String id) {
        log.info("UserService -> retrieveUserById started: userId={}", id);
        if (Boolean.TRUE.equals(StringUtils.isEmpty(id))) throw new ApiBusinessException("userId is a required field.");
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

}
