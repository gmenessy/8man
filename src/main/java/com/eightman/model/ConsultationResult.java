package com.eightman.model;

import com.eightman.server.JsonHelper;
import java.util.ArrayList;
import java.util.List;

public class ConsultationResult {

    private String summary;
    private List<String> proArguments;
    private List<String> contraArguments;
    private List<String> risks;
    private List<String> recommendations;
    private int consensusScore;
    private String eighthManChallenge;
    private List<PhaseResult> phaseResults;

    public ConsultationResult() {
        this.proArguments = new ArrayList<>();
        this.contraArguments = new ArrayList<>();
        this.risks = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.phaseResults = new ArrayList<>();
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getProArguments() { return proArguments; }
    public void setProArguments(List<String> proArguments) { this.proArguments = proArguments; }
    public List<String> getContraArguments() { return contraArguments; }
    public void setContraArguments(List<String> contraArguments) { this.contraArguments = contraArguments; }
    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public int getConsensusScore() { return consensusScore; }
    public void setConsensusScore(int consensusScore) { this.consensusScore = consensusScore; }
    public String getEighthManChallenge() { return eighthManChallenge; }
    public void setEighthManChallenge(String eighthManChallenge) { this.eighthManChallenge = eighthManChallenge; }
    public List<PhaseResult> getPhaseResults() { return phaseResults; }
    public void addPhaseResult(PhaseResult phaseResult) { this.phaseResults.add(phaseResult); }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"summary\":").append(JsonHelper.escapeJsonString(summary)).append(",");
        sb.append("\"proArguments\":").append(JsonHelper.listToJsonArray(proArguments)).append(",");
        sb.append("\"contraArguments\":").append(JsonHelper.listToJsonArray(contraArguments)).append(",");
        sb.append("\"risks\":").append(JsonHelper.listToJsonArray(risks)).append(",");
        sb.append("\"recommendations\":").append(JsonHelper.listToJsonArray(recommendations)).append(",");
        sb.append("\"consensusScore\":").append(consensusScore).append(",");
        sb.append("\"eighthManChallenge\":").append(JsonHelper.escapeJsonString(eighthManChallenge)).append(",");
        sb.append("\"phaseResults\":[");
        for (int i = 0; i < phaseResults.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(phaseResults.get(i).toJson());
        }
        sb.append("]}");
        return sb.toString();
    }
}
