package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.game.LobbyManager;
import com.dimon.discord_bot.model.Lobby;
import com.dimon.discord_bot.model.LobbyEntity;
import com.dimon.discord_bot.service.LobbyService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateLobbyCommand implements ICommand {

    private final LobbyService lobbyService;

    @Autowired
    public CreateLobbyCommand(LobbyService lobbyManager) {
        this.lobbyService = lobbyManager;
    }

    @Override
    public String getName() {
        return "createlobby";
    }

    @Override
    public String getDescription() {
        return "Create a new lobby for the Mafia game.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "players", "Number of players allowed in the lobby", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member creator = event.getMember();
        int maxPlayers = event.getOption("players").getAsInt();

        if (lobbyService.getLobbyByPlayer(creator) != null) {
            event.reply("You are already in a lobby. Please leave your current lobby before creating a new one.").setEphemeral(true).queue();
            return;
        }


        LobbyEntity lobby = lobbyService.createLobby(creator, maxPlayers);
        Button joinButton = Button.primary("join_lobby_" + lobby.getLobbyId(), "Join Lobby");

        event.reply("Lobby created! Max players: " + maxPlayers + "\nShare this link to invite others:")
                .addActionRow(joinButton)
                .queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

}
