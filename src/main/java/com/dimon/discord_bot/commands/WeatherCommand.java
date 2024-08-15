package com.dimon.discord_bot.commands;

import com.dimon.discord_bot.config.ICommand;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class WeatherCommand implements ICommand {

    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";
    private static final String WEATHER_API_KEY = Dotenv.load().get("WEATHER_TOKEN");;

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "Display the weather in your area";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "city", "The city to get weather for").setRequired(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        String cityName = event.getOption("city") != null ?
                event.getOption("city").getAsString()
                : "Please provide your city in the command options.";

        if (!cityName.equals("Please provide your city in the command options.")) {
            String weatherInfo = getWeather(cityName);
            event.reply(weatherInfo).queue();
        } else {
            event.reply(cityName).queue();
        }
    }

    private String getWeather(String city) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(WEATHER_URL, city, WEATHER_API_KEY);

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful()) {
                return "Failed to retrieve weather information";
            }

            String responseData = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseData);

            String weatherDescription = rootNode.get("weather").get(0).get("description").asText();
            double temperature = rootNode.get("main").get("temp").asDouble();
            double feelsLike = rootNode.get("main").get("feels_like").asDouble();
            int humidity = rootNode.get("main").get("humidity").asInt();

            return String.format("Current weather in %s: %s, Temp: %.1f°C, Feels Like: %.1f°C, Humidity: %d%%",
                    city, weatherDescription, temperature, feelsLike, humidity);

        } catch (IOException e) {
            e.printStackTrace();
            return "Error retrieving weather information.";
        }
    }



    @Override
    public void execute(MessageReceivedEvent event) {

    }
}
