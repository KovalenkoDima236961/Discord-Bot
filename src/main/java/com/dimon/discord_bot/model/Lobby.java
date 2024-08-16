package com.dimon.discord_bot.model;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Lobby {
    private final String lobbyId;
    private final Member creator;
    private final int maxPlayers;
    private final List<Member> players = new ArrayList<>();

    public Lobby(String lobbyId, Member creator, int maxPlayers) {
        this.lobbyId = lobbyId;
        this.creator = creator;
        this.maxPlayers = maxPlayers;
        this.players.add(creator);  // The creator is the first player in the lobby
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public void addPlayer(Member player) {
        if (!isFull()) {
            players.add(player);
        }
    }

    public void removePlayer(Member player) {
        players.remove(player);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }
}
