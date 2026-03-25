package com.eightman.model;

import com.eightman.server.JsonHelper;
import java.util.List;

public class ConsultationStatus {

    private String caseId;
    private Phase currentPhase;
    private int progress;
    private boolean complete;
    private boolean failed;
    private String error;
    private ConsultationResult result;
    private List<Agent> agents;

    public ConsultationStatus(String caseId) {
        this.caseId = caseId;
        this.progress = 0;
        this.complete = false;
        this.failed = false;
    }

    public String getCaseId() { return caseId; }
    public Phase getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(Phase currentPhase) { this.currentPhase = currentPhase; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public boolean isComplete() { return complete; }
    public void setComplete(boolean complete) { this.complete = complete; }
    public boolean isFailed() { return failed; }
    public void setFailed(boolean failed) { this.failed = failed; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public ConsultationResult getResult() { return result; }
    public void setResult(ConsultationResult result) { this.result = result; }
    public List<Agent> getAgents() { return agents; }
    public void setAgents(List<Agent> agents) { this.agents = agents; }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"caseId\":").append(JsonHelper.escapeJsonString(caseId)).append(",");
        sb.append("\"currentPhase\":").append(currentPhase != null ? "\"" + currentPhase.name() + "\"" : "null").append(",");
        sb.append("\"progress\":").append(progress).append(",");
        sb.append("\"complete\":").append(complete).append(",");
        sb.append("\"failed\":").append(failed).append(",");
        sb.append("\"error\":").append(error != null ? JsonHelper.escapeJsonString(error) : "null").append(",");
        sb.append("\"result\":").append(result != null ? result.toJson() : "null").append(",");
        sb.append("\"agents\":[");
        if (agents != null) {
            for (int i = 0; i < agents.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(agents.get(i).toJson());
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
