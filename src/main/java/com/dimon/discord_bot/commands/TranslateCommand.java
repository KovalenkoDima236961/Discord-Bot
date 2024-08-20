package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TranslateCommand implements ICommand {
    private static final String TRANSLATE_API_KEY = Dotenv.load().get("TRANSLATE_API_KEY");
    private static final String TRANSLATE_API_URL = "https://translation.googleapis.com/language/translate/v2";

    // Simple in-memory cache: Map<"sourceLang-targetLang-text", "translatedText">
    private final Map<String, String> translationCache = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "translate";
    }

    @Override
    public String getDescription() {
        return "Translate text between different languages.";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "text", "The text you want to translate", true),
                new OptionData(OptionType.STRING, "target_lang", "The language to translate the text to (e.g., 'en', 'fr')", true),
                new OptionData(OptionType.STRING, "source_lang", "The language of the text (optional)", false)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String text = event.getOption("text").getAsString();
        String targetLang = event.getOption("target_lang").getAsString();
        String sourceLang = event.getOption("source_lang") != null ? event.getOption("source_lang").getAsString() : "auto";


        String cacheKey = sourceLang + "-" + targetLang + "-" + text;

        if (translationCache.containsKey(cacheKey)) {
            event.reply("Cached Translation: " + translationCache.get(cacheKey)).queue();
        } else {
            try {
                String translatedText = translate(text, sourceLang, targetLang);
                translationCache.put(cacheKey, translatedText);
                event.reply("Translated Text: " + translatedText).queue();
            } catch (IOException e) {
                event.reply("An error occurred while translating: " + e.getMessage()).queue();
            }
        }
    }

    @Override
    public void execute(MessageReceivedEvent event) {

    }

    private String translate(String text, String sourceLang, String targetLang) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String requestBodyJson = String.format(
                "{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\",\"format\":\"text\"}",
                text, sourceLang, targetLang
        );

        RequestBody body = RequestBody.create(requestBodyJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(TRANSLATE_API_URL + "?key=" + TRANSLATE_API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body().string());
            return rootNode.path("data").path("translations").get(0).path("translatedText").asText();
        }
    }

}
