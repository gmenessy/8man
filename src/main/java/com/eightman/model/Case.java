package com.eightman.model;

import com.eightman.server.JsonHelper;
import java.util.Map;
import java.util.UUID;

public class Case {

    private String id;
    private String title;
    private String description;
    private String question;
    private String goal;
    private long createdAt;

    public Case() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
    }

    public Case(String title, String description, String question, String goal) {
        this();
        this.title = title;
        this.description = description;
        this.question = question;
        this.goal = goal;
    }

    public static Case fromJson(String json) {
        Map<String, String> map = JsonHelper.parseJsonObject(json);
        Case c = new Case();
        if (map.containsKey("title")) c.title = map.get("title");
        if (map.containsKey("description")) c.description = map.get("description");
        if (map.containsKey("question")) c.question = map.get("question");
        if (map.containsKey("goal")) c.goal = map.get("goal");
        return c;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String toJson() {
        return "{" +
            "\"id\":" + JsonHelper.escapeJsonString(id) + "," +
            "\"title\":" + JsonHelper.escapeJsonString(title) + "," +
            "\"description\":" + JsonHelper.escapeJsonString(description) + "," +
            "\"question\":" + JsonHelper.escapeJsonString(question) + "," +
            "\"goal\":" + JsonHelper.escapeJsonString(goal) + "," +
            "\"createdAt\":" + createdAt +
            "}";
    }
}
