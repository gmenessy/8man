package com.eightman.model;

import com.eightman.server.JsonHelper;
import java.util.UUID;

public class Agent {

    private String id;
    private String name;
    private String role;
    private String expertise;
    private String thinkingStyle;
    private String riskTolerance;
    private String argumentationStyle;
    private String modelAssignment;
    private boolean eighthMan;

    public Agent() {
        this.id = UUID.randomUUID().toString();
    }

    public Agent(String name, String role, String expertise, String thinkingStyle,
                 String riskTolerance, String argumentationStyle, String modelAssignment,
                 boolean eighthMan) {
        this();
        this.name = name;
        this.role = role;
        this.expertise = expertise;
        this.thinkingStyle = thinkingStyle;
        this.riskTolerance = riskTolerance;
        this.argumentationStyle = argumentationStyle;
        this.modelAssignment = modelAssignment;
        this.eighthMan = eighthMan;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
    public String getThinkingStyle() { return thinkingStyle; }
    public void setThinkingStyle(String thinkingStyle) { this.thinkingStyle = thinkingStyle; }
    public String getRiskTolerance() { return riskTolerance; }
    public void setRiskTolerance(String riskTolerance) { this.riskTolerance = riskTolerance; }
    public String getArgumentationStyle() { return argumentationStyle; }
    public void setArgumentationStyle(String argumentationStyle) { this.argumentationStyle = argumentationStyle; }
    public String getModelAssignment() { return modelAssignment; }
    public void setModelAssignment(String modelAssignment) { this.modelAssignment = modelAssignment; }
    public boolean isEighthMan() { return eighthMan; }
    public void setEighthMan(boolean eighthMan) { this.eighthMan = eighthMan; }

    public String toJson() {
        return "{" +
            "\"id\":" + JsonHelper.escapeJsonString(id) + "," +
            "\"name\":" + JsonHelper.escapeJsonString(name) + "," +
            "\"role\":" + JsonHelper.escapeJsonString(role) + "," +
            "\"expertise\":" + JsonHelper.escapeJsonString(expertise) + "," +
            "\"thinkingStyle\":" + JsonHelper.escapeJsonString(thinkingStyle) + "," +
            "\"riskTolerance\":" + JsonHelper.escapeJsonString(riskTolerance) + "," +
            "\"argumentationStyle\":" + JsonHelper.escapeJsonString(argumentationStyle) + "," +
            "\"modelAssignment\":" + JsonHelper.escapeJsonString(modelAssignment) + "," +
            "\"eighthMan\":" + eighthMan +
            "}";
    }
}
