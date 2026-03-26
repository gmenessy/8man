package com.eightman.service;

import com.eightman.model.*;

import java.util.List;

public class PromptBuilder {

    public String buildSystemPrompt(Agent agent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Du bist ").append(agent.getName()).append(", ein(e) ").append(agent.getRole());
        sb.append(" mit Expertise in ").append(agent.getExpertise()).append(".\n");
        sb.append("Dein Denkstil ist: ").append(agent.getThinkingStyle()).append(".\n");
        sb.append("Deine Risikoneigung ist: ").append(agent.getRiskTolerance()).append(".\n");
        sb.append("Dein Argumentationsstil ist: ").append(agent.getArgumentationStyle()).append(".\n\n");

        if (agent.isEighthMan()) {
            sb.append("WICHTIGE ROLLE: Du bist der 'Achte Mann' - der systematische Kritiker im Expertenrat.\n");
            sb.append("Deine Aufgabe ist es, die Mehrheitsmeinung in Frage zu stellen, ");
            sb.append("blinde Flecken aufzudecken, Risiken zu identifizieren und ");
            sb.append("konträre Szenarien zu simulieren.\n");
            sb.append("Du MUSST immer eine Gegenposition einnehmen, auch wenn die Mehrheit überzeugend erscheint.\n");
            sb.append("Suche aktiv nach Schwachstellen, unbewiesenen Annahmen und übersehenen Risiken.\n");
        } else {
            sb.append("Du bist Teil eines Expertenrats aus 8 Mitgliedern. ");
            sb.append("Gib deine ehrliche, fundierte Einschätzung basierend auf deiner Expertise.\n");
        }

        sb.append("\nAntworte immer auf Deutsch. Sei strukturiert und prägnant.");
        return sb.toString();
    }

    public String buildAnalysisPrompt(Case caseData) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Akte zur Analyse\n\n");
        sb.append("**Titel:** ").append(caseData.getTitle()).append("\n\n");

        if (notEmpty(caseData.getCategory())) {
            sb.append("**Kategorie:** ").append(caseData.getCategory()).append("\n");
        }
        if (notEmpty(caseData.getPriority())) {
            sb.append("**Priorität:** ").append(caseData.getPriority()).append("\n");
        }
        if (notEmpty(caseData.getIndustry())) {
            sb.append("**Branche:** ").append(caseData.getIndustry()).append("\n");
        }
        sb.append("\n");

        sb.append("**Beschreibung / Kontext:**\n").append(caseData.getDescription()).append("\n\n");

        if (notEmpty(caseData.getBackground())) {
            sb.append("**Hintergrundinformationen:**\n").append(caseData.getBackground()).append("\n\n");
        }

        sb.append("**Konkrete Fragestellung:**\n").append(caseData.getQuestion()).append("\n\n");
        sb.append("**Zieldefinition:**\n").append(caseData.getGoal()).append("\n\n");

        if (notEmpty(caseData.getStakeholders())) {
            sb.append("**Beteiligte Stakeholder:**\n").append(caseData.getStakeholders()).append("\n\n");
        }
        if (notEmpty(caseData.getConstraints())) {
            sb.append("**Rahmenbedingungen & Einschränkungen:**\n").append(caseData.getConstraints()).append("\n\n");
        }
        if (notEmpty(caseData.getTimeframe())) {
            sb.append("**Zeitrahmen:**\n").append(caseData.getTimeframe()).append("\n\n");
        }
        if (notEmpty(caseData.getBudget())) {
            sb.append("**Budget / Ressourcen:**\n").append(caseData.getBudget()).append("\n\n");
        }

        sb.append("---\n\n");
        sb.append("Bitte liefere deine erste Analyse und Einschätzung zu dieser Akte. ");
        sb.append("Berücksichtige dabei deine spezifische Expertise und Perspektive. ");
        sb.append("Beziehe die genannten Stakeholder, Rahmenbedingungen und den Zeitrahmen in deine Bewertung ein.\n\n");
        sb.append("Strukturiere deine Antwort mit:\n");
        sb.append("1. **Kernpunkte** deiner Analyse\n");
        sb.append("2. **Wichtigste Argumente** (inkl. Chancen und Risiken)\n");
        sb.append("3. **Auswirkungen** auf die genannten Stakeholder\n");
        sb.append("4. **Vorläufige Empfehlung**\n");
        return sb.toString();
    }

    private boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public String buildConsensusPrompt(List<AgentResponse> analysisResponses, boolean isEighthMan) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Ergebnisse der Analysephase\n\n");
        sb.append("Die folgenden Experten haben ihre Erstanalyse abgegeben:\n\n");

        for (AgentResponse resp : analysisResponses) {
            sb.append("### ").append(resp.getAgentName()).append(" (").append(resp.getAgentRole()).append(")\n");
            sb.append(resp.getContent()).append("\n\n");
        }

        if (isEighthMan) {
            sb.append("Als Achter Mann: Identifiziere, wo sich Gruppendenken (Groupthink) bildet. ");
            sb.append("Notiere Annahmen, die niemand hinterfragt hat, und bereite deine Gegenargumente vor.\n");
        } else {
            sb.append("Basierend auf allen Analysen:\n");
            sb.append("1. Identifiziere die gemeinsamen Erkenntnisse\n");
            sb.append("2. Benenne Bereiche der Übereinstimmung\n");
            sb.append("3. Markiere Unterschiede in den Bewertungen\n");
            sb.append("4. Formuliere einen Konsensvorschlag\n");
        }
        return sb.toString();
    }

    public String buildDissentPrompt(List<AgentResponse> consensusResponses, boolean isEighthMan) {
        StringBuilder sb = new StringBuilder();

        if (isEighthMan) {
            sb.append("## Konsens der Mehrheit\n\n");
            sb.append("Die folgenden Konsenspunkte wurden erarbeitet:\n\n");
            for (AgentResponse resp : consensusResponses) {
                if (!resp.isEighthMan()) {
                    sb.append("### ").append(resp.getAgentName()).append("\n");
                    sb.append(resp.getContent()).append("\n\n");
                }
            }
            sb.append("Als Achter Mann, liefere deine strukturierte Gegenposition:\n");
            sb.append("1. **Schwächste Annahmen:** Welche Grundannahmen der Mehrheit sind fragwürdig?\n");
            sb.append("2. **Stärkstes Gegenargument:** Was ist das überzeugendste Argument gegen den Konsens?\n");
            sb.append("3. **Übersehene Risiken:** Welche Risiken wurden nicht ausreichend berücksichtigt?\n");
            sb.append("4. **Worst-Case-Szenario:** Was passiert im schlimmsten Fall?\n");
            sb.append("5. **Alternative Hypothesen:** Welche alternativen Erklärungen oder Ansätze gibt es?\n");
        } else {
            sb.append("## Kritik des Achten Mannes\n\n");
            for (AgentResponse resp : consensusResponses) {
                if (resp.isEighthMan()) {
                    sb.append(resp.getContent()).append("\n\n");
                    break;
                }
            }
            sb.append("Der Achte Mann hat den Konsens angegriffen. ");
            sb.append("Reagiere kurz auf diese Kritik aus deiner Expertenperspektive:\n");
            sb.append("- Welche Punkte der Kritik sind berechtigt?\n");
            sb.append("- Wo bleibt deine ursprüngliche Position bestehen?\n");
            sb.append("- Gibt es Anpassungen an deiner Empfehlung?\n");
        }
        return sb.toString();
    }

    public String buildSynthesisPrompt(ConsultationResult partialResult, Case caseData) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Finale Synthese des Expertenrats\n\n");
        sb.append("**Fall:** ").append(caseData.getTitle()).append("\n");
        sb.append("**Fragestellung:** ").append(caseData.getQuestion()).append("\n\n");

        sb.append("Hier sind die Ergebnisse aller Diskussionsphasen:\n\n");

        for (PhaseResult pr : partialResult.getPhaseResults()) {
            sb.append("### Phase: ").append(pr.getPhase().getDisplayName()).append("\n\n");
            for (AgentResponse resp : pr.getResponses()) {
                sb.append("**").append(resp.getAgentName()).append(" (").append(resp.getAgentRole()).append("):**\n");
                sb.append(resp.getContent()).append("\n\n");
            }
        }

        sb.append("Erstelle eine finale, strukturierte Analyse im folgenden JSON-Format. ");
        sb.append("Antworte NUR mit validem JSON, kein anderer Text:\n\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"summary\": \"Zusammenfassung der Gesamtanalyse (2-3 Absätze)\",\n");
        sb.append("  \"proArguments\": [\"Pro-Argument 1\", \"Pro-Argument 2\", ...],\n");
        sb.append("  \"contraArguments\": [\"Contra-Argument 1\", \"Contra-Argument 2\", ...],\n");
        sb.append("  \"risks\": [\"Risiko 1\", \"Risiko 2\", ...],\n");
        sb.append("  \"recommendations\": [\"Empfehlung 1\", \"Empfehlung 2\", ...],\n");
        sb.append("  \"consensusScore\": 75,\n");
        sb.append("  \"eighthManChallenge\": \"Kernargument des Achten Mannes\"\n");
        sb.append("}\n");
        sb.append("```\n");
        return sb.toString();
    }
}
