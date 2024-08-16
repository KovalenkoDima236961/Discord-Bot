package com.dimon.discord_bot.service;

import com.dimon.discord_bot.model.LobbyEntity;
import com.dimon.discord_bot.model.PlayerEntity;
import com.dimon.discord_bot.repository.LobbyRepository;
import com.dimon.discord_bot.repository.PlayerRepository;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LobbyService {
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public LobbyService(LobbyRepository lobbyRepository, PlayerRepository playerRepository) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
    }

    public LobbyEntity createLobby(Member creator, int maxPlayers) {
        UUID lobbyId = UUID.randomUUID();
        LobbyEntity lobby = new LobbyEntity();
        lobby.setLobbyId(lobbyId);
        lobby.setCreatorId(creator.getIdLong());
        lobby.setMaxPlayers(maxPlayers);
        return lobbyRepository.save(lobby);
    }

    public boolean joinLobby(UUID lobbyId, Member player) {
        Optional<LobbyEntity> lobbyOpt = lobbyRepository.findByLobbyId(lobbyId);
        if(lobbyOpt.isEmpty() || lobbyOpt.get().getPlayers().size() >= lobbyOpt.get().getMaxPlayers()) {
            return false;
        }

        if(playerRepository.findByPlayerId(player.getIdLong()).isPresent()) {
            return false;
        }

        LobbyEntity lobby = lobbyOpt.get();
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setPlayerId(player.getIdLong());
        playerEntity.setLobby(lobby);
        playerRepository.save(playerEntity);
        return true;
    }

    public boolean leaveLobby(Member player) {
        Optional<PlayerEntity> playerOpt = playerRepository.findByPlayerId(player.getIdLong());
        if(playerOpt.isPresent()) {
            PlayerEntity playerEntity = playerOpt.get();
            playerRepository.delete(playerEntity);
            return true;
        }
        return false;
    }

    public LobbyEntity getLobbyById(UUID lobbyId) {
        return lobbyRepository.findByLobbyId(lobbyId).orElse(null);
    }

    public LobbyEntity getLobbyByPlayer(Member player) {
        Optional<PlayerEntity> playerEntity = playerRepository.findByPlayerId(player.getIdLong());
        return playerEntity.map(PlayerEntity::getLobby).orElse(null);
    }
}
