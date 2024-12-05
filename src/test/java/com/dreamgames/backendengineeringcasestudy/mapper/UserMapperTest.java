package com.dreamgames.backendengineeringcasestudy.mapper;

import com.dreamgames.backendengineeringcasestudy.entity.User;
import com.dreamgames.backendengineeringcasestudy.model.enums.Country;
import com.dreamgames.backendengineeringcasestudy.model.request.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private CreateUserRequest requestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        requestDTO = new CreateUserRequest();
        user = new User();
    }

    @Test
    void dtoToUser_shouldMapBasicFields() {
        // Arrange
        requestDTO.setUsername("JohnDoe");

        // Act
        UserMapper.dtoToUser(user, requestDTO);

        // Assert
        assertEquals("JohnDoe", user.getUsername(), "Username should match the input.");
        assertEquals(5000, user.getCoins(), "Coins should be initialized to 5000.");
        assertNotNull(user.getCountry(), "Country should not be null.");
        assertTrue(Country.values().length > 0, "There should be at least one country in the enum.");
        assertTrue(isValidCountry(user.getCountry()), "Country should be a valid value from the enum.");
        assertEquals(1, user.getLevel(), "Level should be initialized to 1.");
    }

    @Test
    void dtoToUser_shouldHandleEmptyUsername() {
        // Arrange
        requestDTO.setUsername("");

        // Act
        UserMapper.dtoToUser(user, requestDTO);

        // Assert
        assertEquals("", user.getUsername(), "Username should be mapped even if it's empty.");
        assertEquals(5000, user.getCoins(), "Coins should still be initialized to 5000.");
        assertNotNull(user.getCountry(), "Country should not be null even if username is empty.");
        assertEquals(1, user.getLevel(), "Level should be initialized to 1 even if username is empty.");
    }

    private boolean isValidCountry(Country country) {
        for (Country c : Country.values()) {
            if (c == country) {
                return true;
            }
        }
        return false;
    }
}
