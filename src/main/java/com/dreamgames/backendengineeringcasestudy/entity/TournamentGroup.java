package com.dreamgames.backendengineeringcasestudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tournament_group")
public class TournamentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Boolean competing;

    @OneToMany(mappedBy = "tournamentGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentParticipation> participantUsers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tournament_id", foreignKey = @ForeignKey(name = "fk_tournament_group_tournament"))
    private Tournament tournament;
}
