package com.dimon.discord_bot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "lobbies")
@Getter
@Setter
public class LobbyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lobby_id", unique = true, nullable = false)
    private UUID lobbyId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "max_players", nullable = false)
    private int maxPlayers;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlayerEntity> players;
}
