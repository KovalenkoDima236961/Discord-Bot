package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VoteKickCommand implements ICommand {

    private final StartVoteCommand startVoteCommand;

    @Autowired
    public VoteKickCommand(StartVoteCommand startVoteCommand) {
        this.startVoteCommand = startVoteCommand;
    }

    @Override
    public String getName() {
        return "votekick";
    }

    @Override
    public String getDescription() {
        return "Cast your vote to kick a player out of the lobby.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The player you want to vote to kick", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        startVoteCommand.castVote(event);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        // Not used for message-based command
    }
}