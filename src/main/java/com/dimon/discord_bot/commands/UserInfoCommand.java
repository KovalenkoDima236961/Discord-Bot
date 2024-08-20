package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class UserInfoCommand implements ICommand {
    @Override
    public String getName() {
        return "userinfo";
    }

    @Override
    public String getDescription() {
        return "Displays information about a user. Admins only";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.USER, "target", "The user to display information about", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member admin = event.getMember();

        if (admin != null && admin.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) {
            Member targetMember = event.getOption("target").getAsMember();

            if (targetMember != null) {
                User targetUser = targetMember.getUser();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("User Information")
                        .setColor(Color.BLUE)
                        .setThumbnail(targetUser.getEffectiveAvatarUrl())
                        .addField("Username", targetUser.getName(), false)
                        .addField("Discriminator", "#" + targetUser.getDiscriminator(), false)
                        .addField("ID", targetUser.getId(), false)
                        .addField("Account Created", targetUser.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                        .addField("Joined Server", targetMember.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                        .addField("Roles", targetMember.getRoles().toString(), false)
                        .setFooter("Requested by " + admin.getEffectiveName(), admin.getUser().getEffectiveAvatarUrl());

                event.replyEmbeds(embed.build()).queue();
            } else {
                event.reply("Could not find the target user.").setEphemeral(true).queue();
            }
        } else {
            event.reply("You do not have permission to use this command.").setEphemeral(true).queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {


    }
}
