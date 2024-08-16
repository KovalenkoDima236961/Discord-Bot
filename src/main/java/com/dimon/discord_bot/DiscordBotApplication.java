package com.dimon.discord_bot;

import com.dimon.discord_bot.commands.*;
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
    public CommandLineRunner runBot(CommandManager commandManager,CreateLobbyCommand createLobbyCommand, JoinLobbyCommand joinLobbyCommand,HelpCommand helpCommand ,JokeCommand jokeCommand, ChatGPTCommand chatGPTCommand, AddJokeCommand addJokeCommand, PlayCommand playCommand, NowPlayingCommand nowPlayingCommand, QueueCommand queueCommand, WeatherCommand weatherCommand,RepeatCommand repeatCommand, SkipCommand skipCommand, StopCommand stopCommand) {
        return args -> {
            Dotenv dotenv = Dotenv.load();
            String token = dotenv.get("TOKEN");
            JDA jda = JDABuilder.createDefault(token).build();

            // Register your listeners and command manager
            commandManager.add(jokeCommand);
            commandManager.add(chatGPTCommand);
            commandManager.add(addJokeCommand);
            commandManager.add(playCommand);
            commandManager.add(nowPlayingCommand);
            commandManager.add(queueCommand);
            commandManager.add(helpCommand);
            commandManager.add(repeatCommand);
            commandManager.add(createLobbyCommand);
            commandManager.add(joinLobbyCommand);
            commandManager.add(skipCommand);
            commandManager.add(weatherCommand);
            commandManager.add(stopCommand);
            jda.addEventListener(new Listeners());
            jda.addEventListener(commandManager);

        };
    }
}
