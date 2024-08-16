package com.dimon.discord_bot.model;

import com.dimon.discord_bot.game.RoleAssigner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter
@Setter
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_id")
    private LobbyEntity lobby;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleAssigner.Role role;
}
