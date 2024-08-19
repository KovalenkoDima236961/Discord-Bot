package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NightPhaseCommand implements ICommand {

    private final MafiaVoteCommand mafiaVoteCommand;
    private final DoctorCureCommand doctorCureCommand;
    private final SheriffInvestigateCommand sheriffInvestigateCommand;
    private final HookerBlockCommand hookerBlockCommand;
    private final TallyMafiaVotesCommand tallyMafiaVotesCommand;

    @Autowired
    public NightPhaseCommand(
            MafiaVoteCommand mafiaVoteCommand,
            DoctorCureCommand doctorCureCommand,
            SheriffInvestigateCommand sheriffInvestigateCommand,
            HookerBlockCommand hookerBlockCommand,
            TallyMafiaVotesCommand tallyMafiaVotesCommand
    ) {
        this.mafiaVoteCommand = mafiaVoteCommand;
        this.doctorCureCommand = doctorCureCommand;
        this.sheriffInvestigateCommand = sheriffInvestigateCommand;
        this.hookerBlockCommand = hookerBlockCommand;
        this.tallyMafiaVotesCommand = tallyMafiaVotesCommand;
    }

    @Override
    public String getName() {
        return "startnight";
    }

    @Override
    public String getDescription() {
        return "Starts the night phase where Mafia, Doctor, Sheriff, and Hooker take their actions.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("The night phase has begun! Mafia, please cast your votes.").queue();

        // Trigger the Mafia's turn
        triggerMafiaTurn(event);
    }

    private void triggerMafiaTurn(SlashCommandInteractionEvent event) {
        // Wait for Mafia to vote (you might set up a timer or wait for the votes to be cast)
        event.getChannel().sendMessage("Mafia's turn is over. Doctor, please choose who to cure.").queue();

        // Trigger the Doctor's turn
        triggerDoctorTurn(event);
    }

    private void triggerDoctorTurn(SlashCommandInteractionEvent event) {
        // Wait for the Doctor to choose a player to cure
        event.getChannel().sendMessage("Doctor's turn is over. Sheriff, please choose who to investigate.").queue();

        // Trigger the Sheriff's turn
        triggerSheriffTurn(event);
    }

    private void triggerSheriffTurn(SlashCommandInteractionEvent event) {
        // Wait for the Sheriff to investigate a player
        event.getChannel().sendMessage("Sheriff's turn is over. Hooker, please choose who to block.").queue();

        // Trigger the Hooker's turn
        triggerHookerTurn(event);
    }

    private void triggerHookerTurn(SlashCommandInteractionEvent event) {
        // Wait for the Hooker to block a player
        event.getChannel().sendMessage("Hooker's turn is over. Processing the results...").queue();

        // Process and display the final results
        mafiaVoteCommand.tallyVotesAndAnnounceResult(event, doctorCureCommand, hookerBlockCommand);
    }


    @Override
    public void execute(MessageReceivedEvent event) {

    }
}
