package com.dimon.discord_bot.repository;

import com.dimon.discord_bot.model.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    Optional<PlayerEntity> findByPlayerId(Long playerId);
}
