package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.game.LobbyManager;
import com.dimon.discord_bot.model.Lobby;
import com.dimon.discord_bot.service.LobbyService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JoinLobbyCommand implements ICommand {

    private final LobbyService  lobbyService;

    @Autowired
    public JoinLobbyCommand(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @Override
    public String getName() {
        return "joinlobby";
    }

    @Override
    public String getDescription() {
        return "Join an existing lobby.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        Member player = event.getMember();
        String componentId = event.getComponentId();

        if(!componentId.startsWith("join_lobby_")) {
            return;
        }

        UUID lobbyId = UUID.fromString(componentId.replace("join_lobby_", ""));
        if (lobbyService.getLobbyByPlayer(player) != null) {
            event.reply("You are already in a lobby. Please leave your current lobby before joining another.").setEphemeral(true).queue();
            return;
        }


        boolean joined = lobbyService.joinLobby(lobbyId, player);
        if (joined) {
            event.reply("You have successfully joined the lobby.").queue();
        } else {
            event.reply("The lobby is full or you are already in another lobby.").setEphemeral(true).queue();
        }
    }
}
