package com.dimon.discord_bot.config;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {
    String getName();
    String getDescription();
    List<OptionData> getOptions();
    void execute(SlashCommandInteractionEvent event);
    void execute(MessageReceivedEvent event);
    default void execute(ButtonInteractionEvent event) {
        // Provide a default implementation for commands that don't handle button interactions
        event.reply("This command does not support button interactions.").queue();
    }
}
