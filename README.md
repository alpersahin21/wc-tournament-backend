# Backend Engineering Case Study - Alper Sahin

This document provides an overview of the **World Cup Tournament Backend** implementation for a mobile game backend using **Spring Boot** and **Java**. The service manages users' tournament participation, leaderboard generation, and rewards handling in a gamified environment. Below, you will find a description of the architecture, key components, and design decisions used in the project.

### Key Features

- Users can join tournaments if they meet level and coin requirements.
- Users represent one of the following countries: Turkey, United States, United Kingdom, France, or Germany.
- Tournament groups have 5 users, each from a different country.
- Real-time group and country leaderboards are maintained.
- Users earn rewards after the tournament ends based on their rank.

## Technologies Used

- **Java 17**
- **Spring Boot 3.x** (REST API, Validation, Scheduling, and Transactional Management)
- **MySQL** for storing user, tournament, and participation data
- **JPA/Hibernate** for object-relational mapping
- **Lombok** for reducing boilerplate code
- **Maven** for dependency management

## System Architecture

The system comprises different components, each responsible for specific tasks:

- **User Service**: Manages user data and interactions.
- **Tournament Service**: Handles tournament management, user participation, leaderboard generation, and rewards.
- **Repositories**: Use Spring Data JPA to interact with the database.
- **Exception Handling**: Custom exceptions are used for business rule violations.

## Project Structure

The project is organized into several layers, following clean architecture principles:

- **Entity Layer**: Represents data models for `User`, `Tournament`, `TournamentGroup`, and `TournamentParticipation`.
- **Repository Layer**: Handles database operations for entities.
- **Service Layer**: Contains business logic for managing tournaments and user participation.
- **Controller Layer**: RESTful endpoints to manage user interactions with the system.
- **Mapper Layer**: Utility classes to map DTOs to entities.
- **Exception Layer**: Custom exceptions for handling business rule violations.
- **Model Layer**: Contains DTOs for request and response payloads.
- **Util Layer**: Utility classes for specific needs of business logic.

## Implementation Details

### 1. User Tournament Participation

Users can join ongoing tournaments if they meet eligibility criteria. The following validations are performed:

- The user must be at least level 20.
- The user must have a minimum of 1,000 coins.
- The user must not have any unclaimed rewards from a previous tournament.
- The user must not be already participating in an ongoing tournament.

Once the user is eligible, they are assigned to a tournament group. The assignment process is designed to ensure each group contains users from different countries.

### 2. Tournament Group Assignment

- A user is added to an existing group that doesn't already have a player from the same country.
- If no such group is available, a new group is created and added to the tournament.
- A group can have a maximum of 5 participants, and once full, it is marked as "competing".

### 3. Leaderboard Generation

- **Group Leaderboard**: Displays the ranking of players within a tournament group, including user ID, username, country, score, and rank.
- **Country Leaderboard**: Displays the total scores of participants by country.

### 4. Handling Rewards

After the tournament ends, users can claim their rewards based on their rank in the group. Rewards are as follows:

- **1st Place**: 10,000 coins
- **2nd Place**: 5,000 coins
- **Other Places**: No reward

The `handleClaimReward()` method ensures that the tournament is no longer active and that users can only claim rewards once.

## Key Classes and Methods

- **`TournamentServiceImpl`**: Main class handling tournament-related operations.

    - `addUserToTournament(String userId)`: Adds a user to a tournament group if eligible.
    - `handleClaimReward(String userId)`: Manages reward claims for a user after the tournament.
    - `getGroupLeaderboard(String groupId)`: Generates the leaderboard for a specific group.
    - `getCountryLeaderboard()`: Generates the leaderboard showing the ranking of each country.
    - `updateUserScore(User user)`: Updates the score of a user when they progress in a tournament.
    - `createNewTournament()`: A scheduled method that creates a new tournament each day.

- **`User`**** Entity**: Represents a player in the system. Users have attributes like ID, username, level, coins, and country.

- **`Tournament`**** Entity**: Represents a tournament, which has start and end times and multiple groups.

- **`TournamentGroup`**** Entity**: Represents a group within a tournament, containing up to 5 users from different countries.

- **`TournamentParticipation`**** Entity**: Represents a user's participation in a specific tournament group.

## Scheduling and Transactions

- A scheduled job (`@Scheduled`) is used to create a new tournament every day at midnight.
- `@Transactional` is used to ensure data consistency when updating multiple entities.

## Running the Application
- Make sure to select the container development MySQL URL in the application.properties file.
   ```sh
   docker-compose up --build
   ```

## API Endpoints
Postman collection can be found in project files.
- **`POST /user`**: Creates a new user.
- **`PUT /user/level/{userId}`**: Updates the level of a user.
- **`POST /tournament/enter/{userId}`**: Adds a user to a tournament.
- **`POST /tournament/claim/{userId}`**: Claims the reward for a user.
- **`GET /tournament/rank/{userId}`**: Fetches the ranking of a user in their group.
- **`GET /tournament/leaderboard/group/{groupId}`**: Fetches the leaderboard for specified group.
- **`GET /tournament/leaderboard/country`**: Fetches the leaderboard for all countries.
