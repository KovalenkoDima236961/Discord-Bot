package com.dimon.discord_bot.repository;

import com.dimon.discord_bot.model.Lobby;
import com.dimon.discord_bot.model.LobbyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, Long> {
    Optional<LobbyEntity> findByLobbyId(UUID lobbyId);
}
