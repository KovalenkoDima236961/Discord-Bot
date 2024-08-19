package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TallyMafiaVotesCommand implements ICommand {

    private final MafiaVoteCommand mafiaVoteCommand;
    private final DoctorCureCommand doctorCureCommand;
    private final HookerBlockCommand hookerBlockCommand;

    @Autowired
    public TallyMafiaVotesCommand(MafiaVoteCommand mafiaVoteCommand, DoctorCureCommand doctorCureCommand, HookerBlockCommand hookerBlockCommand) {
        this.mafiaVoteCommand = mafiaVoteCommand;
        this.doctorCureCommand = doctorCureCommand;
        this.hookerBlockCommand = hookerBlockCommand;
    }

    @Override
    public String getName() {
        return "tallyvotes";
    }

    @Override
    public String getDescription() {
        return "Tallies the Mafia's votes and announces the result.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        mafiaVoteCommand.tallyVotesAndAnnounceResult(event, doctorCureCommand, hookerBlockCommand);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        // Not used for message-based command
    }
}