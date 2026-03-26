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
    private String category;       // z.B. Strategie, Technologie, Finanzen, Recht, Personal
    private String industry;       // Branche / Sektor
    private String stakeholders;   // Betroffene Parteien / Stakeholder
    private String constraints;    // Rahmenbedingungen und Einschränkungen
    private String timeframe;      // Zeitrahmen / Dringlichkeit
    private String priority;       // hoch, mittel, niedrig
    private String budget;         // Budget / Ressourcen (optional)
    private String background;     // Zusätzliche Hintergrundinformationen
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
        if (map.containsKey("category")) c.category = map.get("category");
        if (map.containsKey("industry")) c.industry = map.get("industry");
        if (map.containsKey("stakeholders")) c.stakeholders = map.get("stakeholders");
        if (map.containsKey("constraints")) c.constraints = map.get("constraints");
        if (map.containsKey("timeframe")) c.timeframe = map.get("timeframe");
        if (map.containsKey("priority")) c.priority = map.get("priority");
        if (map.containsKey("budget")) c.budget = map.get("budget");
        if (map.containsKey("background")) c.background = map.get("background");
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
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getStakeholders() { return stakeholders; }
    public void setStakeholders(String stakeholders) { this.stakeholders = stakeholders; }
    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }
    public String getTimeframe() { return timeframe; }
    public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String toJson() {
        return "{" +
            "\"id\":" + JsonHelper.escapeJsonString(id) + "," +
            "\"title\":" + JsonHelper.escapeJsonString(title) + "," +
            "\"description\":" + JsonHelper.escapeJsonString(description) + "," +
            "\"question\":" + JsonHelper.escapeJsonString(question) + "," +
            "\"goal\":" + JsonHelper.escapeJsonString(goal) + "," +
            "\"category\":" + JsonHelper.escapeJsonString(category) + "," +
            "\"industry\":" + JsonHelper.escapeJsonString(industry) + "," +
            "\"stakeholders\":" + JsonHelper.escapeJsonString(stakeholders) + "," +
            "\"constraints\":" + JsonHelper.escapeJsonString(constraints) + "," +
            "\"timeframe\":" + JsonHelper.escapeJsonString(timeframe) + "," +
            "\"priority\":" + JsonHelper.escapeJsonString(priority) + "," +
            "\"budget\":" + JsonHelper.escapeJsonString(budget) + "," +
            "\"background\":" + JsonHelper.escapeJsonString(background) + "," +
            "\"createdAt\":" + createdAt +
            "}";
    }
}
