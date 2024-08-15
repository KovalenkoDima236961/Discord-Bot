package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.dimon.discord_bot.model.Joke;
import com.dimon.discord_bot.repository.JokeRepository;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddJokeCommand implements ICommand {

    @Autowired
    private JokeRepository jokeRepository;

    @Override
    public String getName() {
        return "addjoke";
    }

    @Override
    public String getDescription() {
        return "Allows the user to add a joke.";
    }

    @Override
    public List<OptionData> getOptions() {
        // Опція для введення тексту жарту
        return List.of(new OptionData(OptionType.STRING, "joke", "The joke you want to add", false),
                new OptionData(OptionType.ATTACHMENT, "filewithjoke", "Drop your file with jokes", false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping jokeOption = event.getOption("joke");
        if (jokeOption != null) {
            String jokeValue = jokeOption.getAsString();
            jokeRepository.save(new Joke(jokeValue));
            event.reply("Жарт додано: " + jokeValue).queue();
            return;
        }

        // Перевіряємо, чи є вкладений файл
        OptionMapping fileOption = event.getOption("filewithjoke");
        if (fileOption != null) {
            Message.Attachment attachment = fileOption.getAsAttachment();
            if (attachment.getFileExtension().equals("txt")) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(attachment.getUrl()) // Використовуйте правильний URL
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    // Читання вмісту файлу з відповіді сервера
                    String fileContent = response.body().string();
                    List<String> jokes = fileContent.lines()
                            .filter(line -> !line.trim().isEmpty()) // Фільтруємо порожні рядки
                            .collect(Collectors.toList());

                    for (String joke : jokes) {
                        jokeRepository.save(new Joke(joke));
                    }

                    event.reply("Всі жарти з файлу успішно додані!").queue();
                } catch (IOException e) {
                    event.reply("Не вдалося прочитати файл.").queue();
                    e.printStackTrace();
                }
            } else {
                event.reply("Будь ласка, прикріпіть текстовий файл (.txt).").queue();
            }
        } else {
            event.reply("Будь ласка, введіть жарт або прикріпіть текстовий файл з жартами.").queue();
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("/addjoke")) {
            String jokeText = content.replaceFirst("/addjoke", "").trim();

            // Перевіряємо наявність вкладень
            if (!event.getMessage().getAttachments().isEmpty()) {
                for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                    if (attachment.getFileExtension().equals("txt")) {
                        // Завантажуємо файл за допомогою URL
                        try {
                            URL url = new URL(attachment.getProxyUrl());
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                                List<String> jokes = reader.lines()
                                        .filter(line -> !line.trim().isEmpty()) // Фільтруємо порожні рядки
                                        .collect(Collectors.toList());

                                for (String joke : jokes) {
                                    jokeRepository.save(new Joke(joke));
                                }

                                event.getChannel().sendMessage("Всі жарти з файлу успішно додані!").queue();
                            }
                        } catch (Exception e) {
                            event.getChannel().sendMessage("Не вдалося прочитати файл.").queue();
                            e.printStackTrace();
                        }
                        return; // Якщо файл оброблено, не продовжуємо далі
                    } else {
                        event.getChannel().sendMessage("Будь ласка, прикріпіть текстовий файл (.txt).").queue();
                    }
                }
            } else if (!jokeText.isEmpty()) {
                // Якщо вкладення немає, але є текстовий ввід
                jokeRepository.save(new Joke(jokeText));
                event.getChannel().sendMessage("Жарт додано: " + jokeText).queue();
            } else {
                event.getChannel().sendMessage("Будь ласка, введіть жарт або прикріпіть текстовий файл з жартами.").queue();
            }
        }
    }

}
