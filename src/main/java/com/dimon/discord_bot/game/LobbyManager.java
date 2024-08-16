package com.dimon.discord_bot.game;

import com.dimon.discord_bot.model.Lobby;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LobbyManager {
    private final Map<String, Lobby> lobbies = new HashMap<>();
    private final Map<Member, Lobby> playerLobbies = new HashMap<>();

    public Lobby createLobby(Member creator, int maxPlayers) {
        String lobbyId = UUID.randomUUID().toString();
        Lobby lobby = new Lobby(lobbyId, creator, maxPlayers);
        lobbies.put(lobbyId, lobby);
        playerLobbies.put(creator, lobby);
        return lobby;

    }

    public boolean joinLobby(String lobbyId, Member player) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || lobby.isFull()) {
            return false;
        }
        if (playerLobbies.containsKey(player)) {
            return false; // Player already in a lobby
        }
        lobby.addPlayer(player);
        playerLobbies.put(player, lobby);
        return true;
    }

    public boolean leaveLobby(Member player) {
        Lobby lobby = playerLobbies.get(player);
        if (lobby != null) {
            lobby.removePlayer(player);
            playerLobbies.remove(player);
            if (lobby.isEmpty()) {
                lobbies.remove(lobby.getLobbyId());  // Remove empty lobby
            }
            return true;
        }
        return false;
    }

    public Lobby getLobbyById(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public Lobby getLobbyByPlayer(Member player) {
        return playerLobbies.get(player);
    }
}
