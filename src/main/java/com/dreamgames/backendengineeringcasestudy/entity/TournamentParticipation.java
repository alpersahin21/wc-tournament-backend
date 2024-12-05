package com.dreamgames.backendengineeringcasestudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tournament_participation")
public class TournamentParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_tournament_participation_user"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "tournament_group_id", foreignKey = @ForeignKey(name = "fk_tournament_participation_tournament_group"))
    private TournamentGroup tournamentGroup;

    private Integer score;

    private Boolean rewardClaimed;

}
