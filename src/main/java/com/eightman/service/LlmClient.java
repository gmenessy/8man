package com.eightman.service;

import com.eightman.config.AppConfig;
import com.eightman.server.JsonHelper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LlmClient {

    private static final Logger LOGGER = Logger.getLogger(LlmClient.class.getName());
    private final AppConfig config;

    public LlmClient() {
        this.config = AppConfig.getInstance();
    }

    public String chatCompletion(String systemPrompt, String userPrompt, String model) throws IOException {
        String targetModel = (model != null && !model.isEmpty()) ? model : config.getDefaultModel();
        String apiUrl = config.getApiBaseUrl().replaceAll("/+$", "") + "/chat/completions";

        // Build JSON request manually
        String jsonBody = "{" +
            "\"model\":" + JsonHelper.escapeJsonString(targetModel) + "," +
            "\"max_tokens\":" + config.getMaxTokens() + "," +
            "\"temperature\":" + config.getTemperature() + "," +
            "\"messages\":[" +
                "{\"role\":\"system\",\"content\":" + JsonHelper.escapeJsonString(systemPrompt) + "}," +
                "{\"role\":\"user\",\"content\":" + JsonHelper.escapeJsonString(userPrompt) + "}" +
            "]}";

        LOGGER.info("LLM request to model: " + targetModel);

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                String errorBody = readStream(conn.getErrorStream());
                throw new IOException("LLM API error (HTTP " + responseCode + "): " + errorBody);
            }

            String responseBody = readStream(conn.getInputStream());
            return extractContent(responseBody);
        } finally {
            conn.disconnect();
        }
    }

    private String extractContent(String responseBody) throws IOException {
        // Parse: {"choices":[{"message":{"content":"..."}}]}
        Map<String, String> root = JsonHelper.parseJsonObject(responseBody);
        String choicesStr = root.get("choices");
        if (choicesStr == null) {
            throw new IOException("No choices in LLM response");
        }
        List<String> choices = JsonHelper.parseJsonArray(choicesStr);
        if (choices.isEmpty()) {
            throw new IOException("Empty choices in LLM response");
        }
        Map<String, String> choice = JsonHelper.parseJsonObject(choices.get(0));
        String messageStr = choice.get("message");
        if (messageStr == null) {
            throw new IOException("No message in LLM response choice");
        }
        Map<String, String> message = JsonHelper.parseJsonObject(messageStr);
        String content = message.get("content");
        if (content == null) {
            throw new IOException("No content in LLM response message");
        }
        return content;
    }

    private String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        }
    }
}
