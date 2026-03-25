package com.eightman.model;

import java.util.ArrayList;
import java.util.List;

public class PhaseResult {

    private Phase phase;
    private List<AgentResponse> responses;

    public PhaseResult() {
        this.responses = new ArrayList<>();
    }

    public PhaseResult(Phase phase) {
        this();
        this.phase = phase;
    }

    public void addResponse(AgentResponse response) {
        this.responses.add(response);
    }

    public Phase getPhase() { return phase; }
    public List<AgentResponse> getResponses() { return responses; }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"phase\":\"").append(phase != null ? phase.name() : "").append("\",");
        sb.append("\"phaseName\":\"").append(phase != null ? phase.getDisplayName() : "").append("\",");
        sb.append("\"responses\":[");
        for (int i = 0; i < responses.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(responses.get(i).toJson());
        }
        sb.append("]}");
        return sb.toString();
    }
}
