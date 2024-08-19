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

@Component
public class DoctorCureCommand implements ICommand {

    private final PlayerRepository playerRepository;
    private Long curedPlayerId;

    @Autowired
    public DoctorCureCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "doctorcure";
    }

    @Override
    public String getDescription() {
        return "Allows the Doctor to cure a player";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The player you want to cure", true));
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member doctor = event.getMember();
        PlayerEntity doctorEntity = playerRepository.findByPlayerId(doctor.getIdLong()).orElse(null);

        if (doctorEntity == null || doctorEntity.getRole() != RoleAssigner.Role.DOCTOR) {
            event.reply("Only the Doctor can use this command.").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("target").getAsMember();
        if (target == null) {
            event.reply("The target player was not found.").setEphemeral(true).queue();
            return;
        }

        curedPlayerId = target.getIdLong(); // Save the cured player's ID
        event.reply("You have chosen to cure " + target.getEffectiveName() + " tonight.").setEphemeral(true).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

    public Long getCuredPlayerId() {
        return curedPlayerId;
    }

    public void resetCuredPlayer() {
        curedPlayerId = null;
    }
}
