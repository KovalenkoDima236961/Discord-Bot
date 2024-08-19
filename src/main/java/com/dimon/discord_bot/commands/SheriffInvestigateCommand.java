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

import java.util.List;
import java.util.Optional;

@Component
public class SheriffInvestigateCommand implements ICommand {

    private final PlayerRepository playerRepository;

    @Autowired
    public SheriffInvestigateCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "sheriffinvestigate";
    }

    @Override
    public String getDescription() {
        return "Allows the Sheriff to investigate a player.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The player you want to investigate", true));
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member sheriff = event.getMember();
        PlayerEntity sheriffEntity = playerRepository.findByPlayerId(sheriff.getIdLong()).orElse(null);

        if (sheriffEntity == null || sheriffEntity.getRole() != RoleAssigner.Role.SHERIFF) {
            event.reply("Only the Sheriff can use this command.").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("target").getAsMember();
        if(target ==  null) {
            event.reply("The target player was not found.").setEphemeral(true).queue();
            return;
        }

        Optional<PlayerEntity> targetEntityOpt = playerRepository.findByPlayerId(target.getIdLong());
        if (targetEntityOpt.isEmpty()) {
            event.reply("The target player is not part of the game.").setEphemeral(true).queue();
            return;
        }

        PlayerEntity targetEntity = targetEntityOpt.get();
        if (targetEntity.getRole() == RoleAssigner.Role.MAFIA) {
            sheriff.getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("The player " + target.getEffectiveName() + " is a member of the Mafia!").queue();
            });
        } else {
            sheriff.getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("The player " + target.getEffectiveName() + " is not a member of the Mafia.").queue();
            });
        }

        event.reply("You have investigated " + target.getEffectiveName() + ". Check your DMs for the result.").setEphemeral(true).queue();

    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }
}
