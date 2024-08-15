package com.dimon.discord_bot.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Listeners extends ListenerAdapter {

    private static final String OPENAI_API_KEY = Dotenv.load().get("GPT_TOKEN");

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("chatgpt_modal")) {
            String query = event.getValue("query-field").getAsString();
            System.out.println("Received query: " + query);

            event.deferReply().queue();  // Defer the reply to give time for the API request

            try {
                String response = getChatGPTResponse(query);
                event.getHook().sendMessage(response).queue();
            } catch (IOException e) {
                event.getHook().sendMessage("An error occurred while contacting ChatGPT: " + e.getMessage()).queue();
                e.printStackTrace();
            }
        }
    }

    private String getChatGPTResponse(String query) throws IOException {
        OkHttpClient client = new OkHttpClient();
        System.out.println("Sending query to API: " + query);

        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": [{\"role\": \"user\", \"content\": \"" + query + "\"}]\n" +
                "}";
        RequestBody body = RequestBody.create(mediaType, requestBody);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        int maxRetries = 5;
        int retryCount = 0;
        long backoff = 2000L; // Initial backoff time increased to 2 seconds

        while (retryCount < maxRetries) {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    return parseChatGPTResponse(responseData);
                } else if (response.code() == 429) {
                    // Handle rate limit - wait and retry
                    retryCount++;
                    long waitTime = backoff * (long) Math.pow(2, retryCount); // Exponential backoff
                    System.out.println("Rate limit hit. Waiting " + waitTime + " ms before retrying...");
                    Thread.sleep(waitTime);
                } else {
                    throw new IOException("Unexpected response code: " + response);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Reset the interrupt flag
                System.out.println("Request was interrupted. Cleaning up...");
                throw new IOException("Request interrupted", e); // Optionally, rethrow or handle differently
            }
        }

        throw new IOException("Exceeded max retries due to rate limiting");
    }


    private String parseChatGPTResponse(String responseData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseData);

        JsonNode statusNode = root.path("status");
        if (statusNode.isBoolean() && !statusNode.asBoolean()) {
            String errorMessage = root.path("error").path("message").asText();
            throw new IOException("API Error: " + errorMessage);
        }

        JsonNode choicesNode = root.path("choices");
        if (choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode messageNode = choicesNode.get(0).path("message");
            JsonNode contentNode = messageNode.path("content");

            return contentNode.asText().trim();
        } else {
            throw new IOException("Unexpected response structure: " + responseData);
        }
    }


}