package com.dimon.discord_bot;

import com.dimon.discord_bot.commands.AddJokeCommand;
import com.dimon.discord_bot.commands.ChatGPTCommand;
import com.dimon.discord_bot.commands.JokeCommand;
import com.dimon.discord_bot.config.CommandManager;
import com.dimon.discord_bot.config.Listeners;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DiscordBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner runBot(CommandManager commandManager, JokeCommand jokeCommand, ChatGPTCommand chatGPTCommand, AddJokeCommand addJokeCommand) {
        return args -> {
            Dotenv dotenv = Dotenv.load();
            String token = dotenv.get("TOKEN");
            JDA jda = JDABuilder.createDefault(token).build();

            // Register your listeners and command manager
            commandManager.add(jokeCommand);
            commandManager.add(chatGPTCommand);
            commandManager.add(addJokeCommand);
            jda.addEventListener(new Listeners());
            jda.addEventListener(commandManager);

        };
    }
}
