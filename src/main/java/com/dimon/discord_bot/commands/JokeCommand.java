package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.model.Joke;
import com.dimon.discord_bot.repository.JokeRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JokeCommand implements ICommand {

    private JokeRepository jokeRepository;

    @Autowired
    public JokeCommand(JokeRepository repository) {
        this.jokeRepository = repository;
    }

    @Override
    public String getName() {
        return "joke";
    }

    @Override
    public String getDescription() {
        return "Add a joke either by typing it or by uploading a text file.";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;  // No options needed for this command
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        displayJoke(event);
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        displayJoke(event);
    }

    private void displayJoke(Object event) {
        Joke joke = jokeRepository.findRandomJoke();
        if (joke != null) {
            reply(event, joke.getContent());
        } else {
            reply(event, "Sorry, I couldn't find any jokes.");
        }
    }

    private void reply(Object event, String message) {
        if (event instanceof SlashCommandInteractionEvent) {
            ((SlashCommandInteractionEvent) event).reply(message).queue();
        } else if (event instanceof ButtonInteractionEvent) {
            ((ButtonInteractionEvent) event).reply(message).queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {}
}