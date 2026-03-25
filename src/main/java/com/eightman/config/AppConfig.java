package com.eightman.config;

public class AppConfig {

    private static AppConfig instance;

    private final String apiBaseUrl;
    private final String apiKey;
    private final String defaultModel;
    private final String synthesisModel;
    private final int maxTokens;
    private final double temperature;
    private final int serverPort;
    private final int maxParallelCalls;

    private AppConfig() {
        this.apiBaseUrl = env("EIGHTMAN_API_BASE_URL", "https://api.openai.com/v1");
        this.apiKey = env("EIGHTMAN_API_KEY", "");
        this.defaultModel = env("EIGHTMAN_DEFAULT_MODEL", "gpt-4o");
        this.synthesisModel = env("EIGHTMAN_SYNTHESIS_MODEL", env("EIGHTMAN_DEFAULT_MODEL", "gpt-4o"));
        this.maxTokens = Integer.parseInt(env("EIGHTMAN_MAX_TOKENS", "2000"));
        this.temperature = Double.parseDouble(env("EIGHTMAN_TEMPERATURE", "0.7"));
        this.serverPort = Integer.parseInt(env("EIGHTMAN_PORT", "8080"));
        this.maxParallelCalls = Integer.parseInt(env("EIGHTMAN_MAX_PARALLEL", "3"));
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public String getApiBaseUrl() { return apiBaseUrl; }
    public String getApiKey() { return apiKey; }
    public String getDefaultModel() { return defaultModel; }
    public String getSynthesisModel() { return synthesisModel; }
    public int getMaxTokens() { return maxTokens; }
    public double getTemperature() { return temperature; }
    public int getServerPort() { return serverPort; }
    public int getMaxParallelCalls() { return maxParallelCalls; }
}
