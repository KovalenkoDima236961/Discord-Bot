package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetFilmsCommand implements ICommand {

    @Override
    public String getName() {
        return "getfilms";
    }

    @Override
    public String getDescription() {
        return "Get the best films based on genre, year, or author.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Show the user a selection of filters
        event.reply("Select a filter to search for films:")
                .addActionRow(
                        Button.primary("filter_genre", "Genre"),
                        Button.primary("filter_year", "Year"),
                        Button.primary("filter_author", "Author")
                ).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        if (buttonId.equals("filter_genre")) {
            TextInput genreInput = TextInput.create("genre", "Enter Genre", TextInputStyle.SHORT)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("genre_input_modal", "Genre Filter")
                    .addActionRow(genreInput)
                    .build();

            event.replyModal(modal).queue();

        } else if (buttonId.equals("filter_year")) {
            TextInput yearInput = TextInput.create("year", "Enter Year", TextInputStyle.SHORT)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("year_input_modal", "Year Filter")
                    .addActionRow(yearInput)
                    .build();

            event.replyModal(modal).queue();

        } else if (buttonId.equals("filter_author")) {
            TextInput authorInput = TextInput.create("author", "Enter Author", TextInputStyle.SHORT)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("author_input_modal", "Author Filter")
                    .addActionRow(authorInput)
                    .build();

            event.replyModal(modal).queue();
        }
    }
}
