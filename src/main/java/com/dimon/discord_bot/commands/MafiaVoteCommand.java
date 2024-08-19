package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.game.RoleAssigner;
import com.dimon.discord_bot.model.PlayerEntity;
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

@Component
public class MafiaVoteCommand implements ICommand {

    private final PlayerRepository playerRepository;
    private final Map<Long, Long> mafiaVotes = new HashMap<>();

    @Autowired
    public MafiaVoteCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "mafiavote";
    }

    @Override
    public String getDescription() {
        return "Allows Mafia members to vote on who to kill.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The player you want to kill", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member voter = event.getMember();
        PlayerEntity voterEntity = playerRepository.findByPlayerId(voter.getIdLong()).orElse(null);

        if(voterEntity == null || voterEntity.getRole() != RoleAssigner.Role.MAFIA) {
            event.reply("Only Mafia members can use this command.").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("target").getAsMember();
        if (target == null) {
            event.reply("The target player was not found.").setEphemeral(true).queue();
            return;
        }

        mafiaVotes.put(voter.getIdLong(), target.getIdLong());
        event.reply("Your vote has been cast.").setEphemeral(true).queue();
    }

    public void tallyVotesAndAnnounceResult(SlashCommandInteractionEvent event, DoctorCureCommand doctorCureCommand, HookerBlockCommand hookerBlockCommand) {
        if (mafiaVotes.isEmpty()) {
            event.reply("No votes were cast.").queue();
            return;
        }

        Map<Long, Long> voteCounts = new HashMap<>();
        Long blockedPlayerId = hookerBlockCommand.getBlockedPlayerId();

        // Check if any Mafia member was blocked
        for (Map.Entry<Long, Long> entry : mafiaVotes.entrySet()) {
            if (!entry.getKey().equals(blockedPlayerId)) {
                voteCounts.put(entry.getValue(), voteCounts.getOrDefault(entry.getValue(), 0L) + 1);
            }
        }

        Long mostVotedPlayerId = voteCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostVotedPlayerId == null) {
            event.reply("No player was selected for elimination.").queue();
            return;
        }

        Long curedPlayerId = doctorCureCommand.getCuredPlayerId();

        // Check if the most voted player was cured by the Doctor
        if (mostVotedPlayerId.equals(curedPlayerId)) {
            event.getGuild().getTextChannelsByName("mafia-announcements", true)
                    .get(0)
                    .sendMessage("The Mafia attempted to eliminate " + event.getGuild().getMemberById(mostVotedPlayerId).getEffectiveName() + ", but they were saved by the Doctor!")
                    .queue();
        } else {
            Member mostVotedPlayer = event.getGuild().getMemberById(mostVotedPlayerId);
            if (mostVotedPlayer != null) {
                event.getGuild().getTextChannelsByName("mafia-announcements", true)
                        .get(0)
                        .sendMessage(mostVotedPlayer.getEffectiveName() + " has been eliminated by the Mafia.")
                        .queue();

                // Optionally: Remove the player from the game, assign them a dead role, etc.
            }
        }

        resetVotes(); // Clear the votes for the next round
        doctorCureCommand.resetCuredPlayer(); // Reset the Doctor's choice for the next round
        hookerBlockCommand.resetBlockedPlayer(); // Reset the Hooker's block for the next round
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

    public void resetVotes() {
        mafiaVotes.clear();
    }


}
