package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.game.RoleAssigner;
import com.dimon.discord_bot.model.LobbyEntity;
import com.dimon.discord_bot.model.PlayerEntity;
import com.dimon.discord_bot.repository.LobbyRepository;
import com.dimon.discord_bot.repository.PlayerRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StartVoteCommand implements ICommand {

    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final Map<Long, Long> voteCounts = new HashMap<>();

    @Autowired
    public StartVoteCommand(LobbyRepository lobbyRepository, PlayerRepository playerRepository) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "startvote";
    }

    @Override
    public String getDescription() {
        return "Allows the room owner to start a vote to kick a player out of the lobby.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The player you want to vote to kick", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member owner = event.getMember();
        LobbyEntity lobby = lobbyRepository.findByCreatorId(owner.getIdLong()).orElse(null);

        if (lobby == null || !lobby.getCreatorId().equals(owner.getIdLong())) {
            event.reply("You are not the owner of any lobby or your lobby doesn't exist.").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("target").getAsMember();
        if (target == null) {
            event.reply("The target player was not found.").setEphemeral(true).queue();
            return;
        }

        // Start a vote
        event.reply("A vote has started to kick " + target.getEffectiveName() + " out of the lobby. Players, please cast your votes!").queue();

        // Initialize vote counts
        voteCounts.clear();
        List<Member> members = lobby.getPlayers().stream()
                .map(playerEntity -> event.getGuild().getMemberById(playerEntity.getPlayerId()))
                .collect(Collectors.toList());

        for (Member member : members) {
            voteCounts.put(member.getIdLong(), 0L);
        }

        event.getGuild().getTextChannelsByName("mafia-announcements", true).get(0)
                .sendMessage("Use /votekick to cast your vote!").queue();
    }

    public void castVote(SlashCommandInteractionEvent event) {
        Member voter = event.getMember();
        PlayerEntity voterEntity = playerRepository.findByPlayerId(voter.getIdLong()).orElse(null);

        if (voterEntity == null || voterEntity.getLobby() == null) {
            event.reply("You are not in a lobby.").setEphemeral(true).queue();
            return;
        }

        LobbyEntity lobby = voterEntity.getLobby();
        Member target = event.getOption("target").getAsMember();

        if (target == null) {
            event.reply("The target player was not found.").setEphemeral(true).queue();
            return;
        }

        if (!voteCounts.containsKey(voter.getIdLong())) {
            event.reply("You are not part of this lobby.").setEphemeral(true).queue();
            return;
        }

        // Check if the voter has already voted
        if (voteCounts.get(voter.getIdLong()) > 0) {
            event.reply("You have already cast your vote.").setEphemeral(true).queue();
            return;
        }

        voteCounts.put(target.getIdLong(), voteCounts.get(target.getIdLong()) + 1);
        event.reply("Your vote has been cast to kick " + target.getEffectiveName() + ".").setEphemeral(true).queue();

        // Check if all players have voted
        if (voteCounts.values().stream().mapToLong(Long::longValue).sum() >= lobby.getPlayers().size()) {
            tallyVotes(event,lobby);
        }
    }

    private void tallyVotes(SlashCommandInteractionEvent event, LobbyEntity lobby) {
        // Determine the player with the most votes
        Long mostVotedPlayerId = voteCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostVotedPlayerId == null) {
            event.reply("No player received enough votes to be kicked.").queue();
            return;
        }

        PlayerEntity kickedPlayer = playerRepository.findByPlayerId(mostVotedPlayerId).orElse(null);

        if (kickedPlayer == null) {
            event.reply("The voted player was not found in the lobby.").queue();
            return;
        }

        if (kickedPlayer.getRole() == RoleAssigner.Role.MAFIA) {
            event.getGuild().getTextChannelsByName("mafia-announcements", true)
                    .get(0)
                    .sendMessage("The player " + event.getGuild().getMemberById(mostVotedPlayerId).getEffectiveName() + " was Mafia and has been kicked!").queue();

            // Remove the player from the PlayerRepository
            playerRepository.delete(kickedPlayer);

            // Check if any Mafia are left
            boolean mafiaLeft = lobby.getPlayers().stream().anyMatch(player -> player.getRole() == RoleAssigner.Role.MAFIA);

            if (!mafiaLeft) {
                event.getGuild().getTextChannelsByName("mafia-announcements", true)
                        .get(0)
                        .sendMessage("All Mafia members have been eliminated! The Town wins!").queue();
                endGame(event);
            } else {
                event.getGuild().getTextChannelsByName("mafia-announcements", true)
                        .get(0)
                        .sendMessage("There are still Mafia members left. The game continues.").queue();
            }
        } else {
            event.getGuild().getTextChannelsByName("mafia-announcements", true)
                    .get(0)
                    .sendMessage("The player " + event.getGuild().getMemberById(mostVotedPlayerId).getEffectiveName() + " was not Mafia but has been kicked!").queue();

            // Remove the player from the PlayerRepository
            playerRepository.delete(kickedPlayer);
        }

        resetVotes();
    }

    private void endGame(SlashCommandInteractionEvent event) {
        // Logic to end the game, clean up resources, and reset the lobby
    }

    private void resetVotes() {
        voteCounts.clear();
    }


    @Override
    public void execute(MessageReceivedEvent event) {

    }
}
