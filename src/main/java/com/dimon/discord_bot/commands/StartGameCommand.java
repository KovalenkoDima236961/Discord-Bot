package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.game.RoleAssigner;
import com.dimon.discord_bot.model.LobbyEntity;
import com.dimon.discord_bot.model.PlayerEntity;
import com.dimon.discord_bot.repository.PlayerRepository;
import com.dimon.discord_bot.service.LobbyService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StartGameCommand implements ICommand {

    private final LobbyService lobbyService;
    private final PlayerRepository playerRepository;

    @Autowired
    public StartGameCommand(LobbyService lobbyService, PlayerRepository playerRepository) {
        this.lobbyService = lobbyService;
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "startgame";
    }

    @Override
    public String getDescription() {
        return "Starts the Mafia game in your lobby";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member creator = event.getMember();
        LobbyEntity lobby = lobbyService.getLobbyByPlayer(creator);

        if (lobby == null || !lobby.getCreatorId().equals(creator.getIdLong())) {
            event.reply("You are not the owner of any lobby or your lobby doesn't exist.").setEphemeral(true).queue();
            return;
        }

        List<PlayerEntity> players = lobby.getPlayers().stream().collect(Collectors.toList());

        if (players.size() < 5) { // You can adjust this minimum player count as needed
            event.reply("Not enough players to start the game.").setEphemeral(true).queue();
            return;
        }


        // Assign roles based on number of players
        int mafiaCount = 1; // Example: 1 Mafia
        int doctorCount = 1; // Example: 1 Doctor
        int sheriffCount = 1; // Example: 1 Sheriff
        int hookerCount = 1; // Example: 1 Hooker
        int peacefulCitizenCount = players.size() - (mafiaCount + doctorCount + sheriffCount + hookerCount); // Remaining are Peaceful Citizens

        List<RoleAssigner.Role> roles = RoleAssigner.assignRoles(mafiaCount, doctorCount, sheriffCount, hookerCount, peacefulCitizenCount);

        for (int i = 0; i < players.size(); i++) {
            PlayerEntity player = players.get(i);
            RoleAssigner.Role role = roles.get(i);

            // Save the role in the database
            player.setRole(role);
            playerRepository.save(player);

            // Send role to player
            Member member = event.getGuild().getMemberById(player.getPlayerId());
            if (member != null) {
                member.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Your role is: " + role).queue();
                });
            }
        }

        event.reply("The game has started! Roles have been sent to all players.").queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }
}
