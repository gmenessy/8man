package com.eightman.model;

import com.eightman.server.JsonHelper;

public class AgentResponse {

    private String agentId;
    private String agentName;
    private String agentRole;
    private boolean eighthMan;
    private String content;

    public AgentResponse() {}

    public AgentResponse(Agent agent, String content) {
        this.agentId = agent.getId();
        this.agentName = agent.getName();
        this.agentRole = agent.getRole();
        this.eighthMan = agent.isEighthMan();
        this.content = content;
    }

    public String getAgentId() { return agentId; }
    public String getAgentName() { return agentName; }
    public String getAgentRole() { return agentRole; }
    public boolean isEighthMan() { return eighthMan; }
    public String getContent() { return content; }

    public String toJson() {
        return "{" +
            "\"agentId\":" + JsonHelper.escapeJsonString(agentId) + "," +
            "\"agentName\":" + JsonHelper.escapeJsonString(agentName) + "," +
            "\"agentRole\":" + JsonHelper.escapeJsonString(agentRole) + "," +
            "\"eighthMan\":" + eighthMan + "," +
            "\"content\":" + JsonHelper.escapeJsonString(content) +
            "}";
    }
}
