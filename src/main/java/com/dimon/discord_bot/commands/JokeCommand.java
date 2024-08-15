package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.model.Joke;
import com.dimon.discord_bot.repository.JokeRepository;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
        Joke joke = jokeRepository.findRandomJoke();
        if (joke != null) {
            event.reply(joke.getContent()).queue();
        } else {
            event.reply("Sorry, I couldn't find any jokes.").queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {}
}