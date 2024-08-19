package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.game.RoleAssigner;
import com.dimon.discord_bot.model.PlayerEntity;
import com.dimon.discord_bot.repository.PlayerRepository;
import lombok.Getter;
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
@Getter
public class HookerBlockCommand implements ICommand {

    private final PlayerRepository playerRepository;
    private Long blockedPlayerId; // Stores the ID of the player blocked by the Hooker

    @Autowired
    public HookerBlockCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public String getName() {
        return "hookerblock";
    }

    @Override
    public String getDescription() {
        return "Allows the Hooker to block a player, preventing them from taking their action.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The player you want to block", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member hooker = event.getMember();
        PlayerEntity hookerEntity = playerRepository.findByPlayerId(hooker.getIdLong()).orElse(null);

        if(hookerEntity == null || hookerEntity.getRole() != RoleAssigner.Role.HOOKER) {
            event.reply("Only the Hooker can use this command.").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("target").getAsMember();
        if (target == null) {
            event.reply("The target player was not found.").setEphemeral(true).queue();
            return;
        }

        Optional<PlayerEntity> targetEntityOpt = playerRepository.findByPlayerId(target.getIdLong());
        if(targetEntityOpt.isEmpty()) {
            event.reply("The target player is not part of the game.").setEphemeral(true).queue();
            return;
        }

        blockedPlayerId = target.getIdLong();
        event.reply("You have blocked " + target.getEffectiveName() + " from taking their action tonight.").setEphemeral(true).queue();

    }

    public void resetBlockedPlayer() {
        blockedPlayerId = null;
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }
}
