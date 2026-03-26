package com.eightman;

import com.eightman.model.*;
import com.eightman.service.PromptBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.eightman.TestRunner.*;

public class PromptBuilderTest {

    public static void runAll() {
        testSystemPromptConsensusAgent();
        testSystemPromptEighthMan();
        testAnalysisPromptBasic();
        testAnalysisPromptExtended();
        testAnalysisPromptOptionalFieldsOmitted();
        testConsensusPromptConsensus();
        testConsensusPromptEighthMan();
        testDissentPromptEighthMan();
        testDissentPromptConsensusResponse();
        testSynthesisPrompt();
    }

    private static PromptBuilder builder() {
        return new PromptBuilder();
    }

    private static Agent consensusAgent() {
        return new Agent("Dr. Voss", "Strategin", "Planung", "analytisch",
                "mittel", "datengetrieben", "gpt-4o", false);
    }

    private static Agent eighthManAgent() {
        return new Agent("Der Achte Mann", "Systematischer Kritiker",
                "Kritische Analyse", "konträr", "entgegengesetzt",
                "Advocatus Diaboli", "gpt-4o", true);
    }

    static void testSystemPromptConsensusAgent() {
        String prompt = builder().buildSystemPrompt(consensusAgent());
        assertContains("SystemPrompt Konsens: Name", prompt, "Dr. Voss");
        assertContains("SystemPrompt Konsens: Rolle", prompt, "Strategin");
        assertContains("SystemPrompt Konsens: Expertise", prompt, "Planung");
        assertContains("SystemPrompt Konsens: Denkstil", prompt, "analytisch");
        assertContains("SystemPrompt Konsens: Deutsch", prompt, "Deutsch");
        // Sollte NICHT den Achter-Mann-Text enthalten
        assertFalse("SystemPrompt Konsens: kein Achter-Mann-Text",
                prompt.contains("Achte Mann"));
    }

    static void testSystemPromptEighthMan() {
        String prompt = builder().buildSystemPrompt(eighthManAgent());
        assertContains("SystemPrompt 8Mann: Name", prompt, "Der Achte Mann");
        assertContains("SystemPrompt 8Mann: Rolle", prompt, "Achte Mann");
        assertContains("SystemPrompt 8Mann: Gegenposition", prompt, "Gegenposition");
        assertContains("SystemPrompt 8Mann: blinde Flecken", prompt, "blinde Flecken");
        assertContains("SystemPrompt 8Mann: Schwachstellen", prompt, "Schwachstellen");
    }

    static void testAnalysisPromptBasic() {
        Case c = new Case("Markteintritt China", "Wir erwägen Expansion",
                "Sollen wir expandieren?", "Entscheidungsgrundlage");
        String prompt = builder().buildAnalysisPrompt(c);
        assertContains("AnalysePrompt: Titel", prompt, "Markteintritt China");
        assertContains("AnalysePrompt: Beschreibung", prompt, "Wir erwägen Expansion");
        assertContains("AnalysePrompt: Fragestellung", prompt, "Sollen wir expandieren?");
        assertContains("AnalysePrompt: Ziel", prompt, "Entscheidungsgrundlage");
        assertContains("AnalysePrompt: Strukturhinweis", prompt, "Kernpunkte");
    }

    static void testAnalysisPromptExtended() {
        Case c = new Case("Test", "Beschr.", "Frage?", "Ziel");
        c.setCategory("Strategie");
        c.setPriority("Hoch");
        c.setIndustry("FinTech");
        c.setStakeholders("Vorstand, Kunden, Regulierer");
        c.setConstraints("BAFIN-Regulierung");
        c.setTimeframe("Q3 2026");
        c.setBudget("500.000 EUR");
        c.setBackground("Marktstudie von 2025");

        String prompt = builder().buildAnalysisPrompt(c);
        assertContains("AnalysePrompt ext: Kategorie", prompt, "Strategie");
        assertContains("AnalysePrompt ext: Priorität", prompt, "Hoch");
        assertContains("AnalysePrompt ext: Branche", prompt, "FinTech");
        assertContains("AnalysePrompt ext: Stakeholder", prompt, "Vorstand, Kunden, Regulierer");
        assertContains("AnalysePrompt ext: Rahmenbedingungen", prompt, "BAFIN-Regulierung");
        assertContains("AnalysePrompt ext: Zeitrahmen", prompt, "Q3 2026");
        assertContains("AnalysePrompt ext: Budget", prompt, "500.000 EUR");
        assertContains("AnalysePrompt ext: Hintergrund", prompt, "Marktstudie von 2025");
        assertContains("AnalysePrompt ext: Stakeholder-Hinweis", prompt, "Stakeholder");
    }

    static void testAnalysisPromptOptionalFieldsOmitted() {
        Case c = new Case("Minimal", "Kontext", "Frage?", "Ziel");
        // Keine optionalen Felder gesetzt
        String prompt = builder().buildAnalysisPrompt(c);
        assertFalse("AnalysePrompt minimal: keine Branche",
                prompt.contains("Branche"));
        assertFalse("AnalysePrompt minimal: kein Budget",
                prompt.contains("Budget"));
    }

    static void testConsensusPromptConsensus() {
        List<AgentResponse> responses = createSampleResponses();
        String prompt = builder().buildConsensusPrompt(responses, false);
        assertContains("KonsensPrompt: enthält Analyseergebnisse", prompt, "Analysephase");
        assertContains("KonsensPrompt: enthält Agent-Namen", prompt, "Agent A");
        assertContains("KonsensPrompt: Konsensvorschlag", prompt, "Konsensvorschlag");
    }

    static void testConsensusPromptEighthMan() {
        List<AgentResponse> responses = createSampleResponses();
        String prompt = builder().buildConsensusPrompt(responses, true);
        assertContains("KonsensPrompt 8Mann: Groupthink", prompt, "Gruppendenken");
        assertContains("KonsensPrompt 8Mann: Annahmen", prompt, "Annahmen");
    }

    static void testDissentPromptEighthMan() {
        List<AgentResponse> responses = createSampleResponses();
        String prompt = builder().buildDissentPrompt(responses, true);
        assertContains("DissentPrompt 8Mann: Schwächste Annahmen", prompt, "Schwächste Annahmen");
        assertContains("DissentPrompt 8Mann: Worst-Case", prompt, "Worst-Case");
        assertContains("DissentPrompt 8Mann: Alternative", prompt, "Alternative Hypothesen");
    }

    static void testDissentPromptConsensusResponse() {
        // Erstelle Responses mit einem Achter-Mann-Eintrag
        List<AgentResponse> responses = new ArrayList<>();
        Agent eighth = eighthManAgent();
        responses.add(new AgentResponse(eighth, "Meine Kritik ist..."));
        responses.add(new AgentResponse(consensusAgent(), "Mein Konsens..."));

        String prompt = builder().buildDissentPrompt(responses, false);
        assertContains("DissentPrompt Konsens: enthält Kritik", prompt, "Meine Kritik ist...");
        assertContains("DissentPrompt Konsens: berechtigt", prompt, "berechtigt");
    }

    static void testSynthesisPrompt() {
        ConsultationResult result = new ConsultationResult();
        PhaseResult pr = new PhaseResult(Phase.ANALYSIS);
        pr.addResponse(new AgentResponse(consensusAgent(), "Analyse-Inhalt"));
        result.addPhaseResult(pr);

        Case c = new Case("Synthese-Test", "Beschr.", "Frage?", "Ziel");
        String prompt = builder().buildSynthesisPrompt(result, c);
        assertContains("SynthesePrompt: Titel", prompt, "Synthese-Test");
        assertContains("SynthesePrompt: Fragestellung", prompt, "Frage?");
        assertContains("SynthesePrompt: JSON-Schema", prompt, "proArguments");
        assertContains("SynthesePrompt: JSON-Schema", prompt, "contraArguments");
        assertContains("SynthesePrompt: JSON-Schema", prompt, "risks");
        assertContains("SynthesePrompt: JSON-Schema", prompt, "recommendations");
        assertContains("SynthesePrompt: JSON-Schema", prompt, "consensusScore");
        assertContains("SynthesePrompt: JSON-Schema", prompt, "eighthManChallenge");
    }

    private static List<AgentResponse> createSampleResponses() {
        List<AgentResponse> responses = new ArrayList<>();
        Agent a1 = new Agent("Agent A", "Analyst", "Finanzen", "analytisch",
                "konservativ", "evidenzbasiert", "gpt-4o", false);
        Agent a2 = new Agent("Agent B", "Jurist", "Recht", "präzise",
                "konservativ", "argumentativ", "gpt-4o", false);
        responses.add(new AgentResponse(a1, "Meine Finanzanalyse zeigt..."));
        responses.add(new AgentResponse(a2, "Aus rechtlicher Sicht..."));
        return responses;
    }
}
