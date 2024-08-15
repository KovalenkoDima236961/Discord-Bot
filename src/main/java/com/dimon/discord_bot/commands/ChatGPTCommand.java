package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatGPTCommand implements ICommand {


    @Override
    public String getName() {
        return "chatgpt";
    }

    @Override
    public String getDescription() {
        return "Send a query to ChatGPT and get a response.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        TextInput query = TextInput.create("query-field", "Query", TextInputStyle.PARAGRAPH)
                .setRequired(true)
                .setMinLength(1)
                .setPlaceholder("Enter your prompt")
                .build();
        Modal modal = Modal.create("chatgpt_modal", "ChatGPT Query")
                .addComponents(ActionRow.of(query))
                .build();
        event.replyModal(modal).queue();
    }

    @Override
    public void execute(MessageReceivedEvent event) {}
}