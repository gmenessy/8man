package com.eightman.service;

import com.eightman.config.AppConfig;
import com.eightman.model.*;
import com.eightman.server.JsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultationService {

    private static final Logger LOGGER = Logger.getLogger(ConsultationService.class.getName());
    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;
    private final AgentGeneratorService agentGenerator;
    private final ExecutorService executor;
    private final Map<String, ConsultationStatus> statusMap = new ConcurrentHashMap<>();

    public ConsultationService() {
        this.llmClient = new LlmClient();
        this.promptBuilder = new PromptBuilder();
        this.agentGenerator = new AgentGeneratorService();
        int maxParallel = AppConfig.getInstance().getMaxParallelCalls();
        this.executor = Executors.newFixedThreadPool(maxParallel);
    }

    public ConsultationStatus startConsultation(Case caseData) {
        ConsultationStatus status = new ConsultationStatus(caseData.getId());
        List<Agent> agents = agentGenerator.generateCouncil();
        status.setAgents(agents);
        statusMap.put(caseData.getId(), status);

        CompletableFuture.runAsync(() -> runConsultation(caseData, agents, status), executor);

        return status;
    }

    public ConsultationStatus getStatus(String caseId) {
        return statusMap.get(caseId);
    }

    private void runConsultation(Case caseData, List<Agent> agents, ConsultationStatus status) {
        try {
            ConsultationResult result = new ConsultationResult();

            // Phase 1: Analysis
            status.setCurrentPhase(Phase.ANALYSIS);
            status.setProgress(5);
            PhaseResult analysisResult = runAnalysisPhase(caseData, agents);
            result.addPhaseResult(analysisResult);
            status.setProgress(25);

            // Phase 2: Consensus
            status.setCurrentPhase(Phase.CONSENSUS);
            status.setProgress(30);
            PhaseResult consensusResult = runConsensusPhase(analysisResult.getResponses(), agents);
            result.addPhaseResult(consensusResult);
            status.setProgress(50);

            // Phase 3: Dissent
            status.setCurrentPhase(Phase.DISSENT);
            status.setProgress(55);
            PhaseResult dissentResult = runDissentPhase(consensusResult.getResponses(), agents);
            result.addPhaseResult(dissentResult);
            status.setProgress(75);

            // Phase 4: Synthesis
            status.setCurrentPhase(Phase.SYNTHESIS);
            status.setProgress(80);
            synthesizeResult(result, caseData);
            status.setProgress(100);

            status.setResult(result);
            status.setComplete(true);
            LOGGER.info("Consultation completed for case: " + caseData.getId());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Consultation failed", e);
            status.setFailed(true);
            status.setError(e.getMessage());
        }
    }

    private PhaseResult runAnalysisPhase(Case caseData, List<Agent> agents) {
        PhaseResult phaseResult = new PhaseResult(Phase.ANALYSIS);
        String userPrompt = promptBuilder.buildAnalysisPrompt(caseData);

        List<Future<AgentResponse>> futures = new ArrayList<>();
        for (Agent agent : agents) {
            futures.add(executor.submit(() -> callAgent(agent, userPrompt)));
        }

        collectResponses(futures, phaseResult);
        return phaseResult;
    }

    private PhaseResult runConsensusPhase(List<AgentResponse> analysisResponses, List<Agent> agents) {
        PhaseResult phaseResult = new PhaseResult(Phase.CONSENSUS);

        List<Future<AgentResponse>> futures = new ArrayList<>();
        for (Agent agent : agents) {
            String userPrompt = promptBuilder.buildConsensusPrompt(analysisResponses, agent.isEighthMan());
            futures.add(executor.submit(() -> callAgent(agent, userPrompt)));
        }

        collectResponses(futures, phaseResult);
        return phaseResult;
    }

    private PhaseResult runDissentPhase(List<AgentResponse> consensusResponses, List<Agent> agents) {
        PhaseResult phaseResult = new PhaseResult(Phase.DISSENT);

        Agent eighthMan = null;
        for (Agent a : agents) {
            if (a.isEighthMan()) {
                eighthMan = a;
                break;
            }
        }

        if (eighthMan != null) {
            String dissentPrompt = promptBuilder.buildDissentPrompt(consensusResponses, true);
            try {
                AgentResponse dissentResponse = callAgent(eighthMan, dissentPrompt);
                phaseResult.addResponse(dissentResponse);

                List<AgentResponse> withDissent = new ArrayList<>(consensusResponses);
                withDissent.add(dissentResponse);

                List<Future<AgentResponse>> futures = new ArrayList<>();
                for (Agent agent : agents) {
                    if (!agent.isEighthMan()) {
                        String responsePrompt = promptBuilder.buildDissentPrompt(withDissent, false);
                        futures.add(executor.submit(() -> callAgent(agent, responsePrompt)));
                    }
                }
                collectResponses(futures, phaseResult);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Dissent phase error", e);
            }
        }

        return phaseResult;
    }

    private void synthesizeResult(ConsultationResult result, Case caseData) {
        String synthesisModel = AppConfig.getInstance().getSynthesisModel();
        String systemPrompt = "Du bist ein erfahrener Moderator eines Expertenrats. " +
                "Deine Aufgabe ist es, alle Perspektiven zu einer finalen, strukturierten Analyse zusammenzufassen. " +
                "Antworte NUR mit validem JSON.";
        String userPrompt = promptBuilder.buildSynthesisPrompt(result, caseData);

        try {
            String response = llmClient.chatCompletion(systemPrompt, userPrompt, synthesisModel);
            parseSynthesisResponse(response, result);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Synthesis failed, creating fallback", e);
            createFallbackSynthesis(result);
        }
    }

    private void parseSynthesisResponse(String response, ConsultationResult result) {
        try {
            String json = response;
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}");
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                json = response.substring(jsonStart, jsonEnd + 1);
            }

            Map<String, String> obj = JsonHelper.parseJsonObject(json);

            if (obj.containsKey("summary")) {
                result.setSummary(obj.get("summary"));
            }
            if (obj.containsKey("proArguments")) {
                result.setProArguments(JsonHelper.parseJsonArray(obj.get("proArguments")));
            }
            if (obj.containsKey("contraArguments")) {
                result.setContraArguments(JsonHelper.parseJsonArray(obj.get("contraArguments")));
            }
            if (obj.containsKey("risks")) {
                result.setRisks(JsonHelper.parseJsonArray(obj.get("risks")));
            }
            if (obj.containsKey("recommendations")) {
                result.setRecommendations(JsonHelper.parseJsonArray(obj.get("recommendations")));
            }
            if (obj.containsKey("consensusScore")) {
                try {
                    result.setConsensusScore(Integer.parseInt(obj.get("consensusScore").trim()));
                } catch (NumberFormatException e) {
                    result.setConsensusScore(50);
                }
            }
            if (obj.containsKey("eighthManChallenge")) {
                result.setEighthManChallenge(obj.get("eighthManChallenge"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse synthesis JSON", e);
            createFallbackSynthesis(result);
        }
    }

    private void createFallbackSynthesis(ConsultationResult result) {
        result.setSummary("Die automatische Synthese konnte nicht erstellt werden. " +
                "Bitte prüfen Sie die einzelnen Phasenergebnisse.");
        result.setConsensusScore(50);
    }

    private AgentResponse callAgent(Agent agent, String userPrompt) {
        String systemPrompt = promptBuilder.buildSystemPrompt(agent);
        try {
            String content = llmClient.chatCompletion(systemPrompt, userPrompt, agent.getModelAssignment());
            return new AgentResponse(agent, content);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Agent call failed for " + agent.getName(), e);
            return new AgentResponse(agent, "[Fehler: Agent konnte nicht antworten - " + e.getMessage() + "]");
        }
    }

    private void collectResponses(List<Future<AgentResponse>> futures, PhaseResult phaseResult) {
        for (Future<AgentResponse> future : futures) {
            try {
                phaseResult.addResponse(future.get(180, TimeUnit.SECONDS));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to collect agent response", e);
            }
        }
    }
}
