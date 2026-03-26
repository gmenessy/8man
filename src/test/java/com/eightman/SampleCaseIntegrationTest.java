package com.eightman;

import com.eightman.model.*;
import com.eightman.server.JsonHelper;
import com.eightman.service.AgentGeneratorService;
import com.eightman.service.PromptBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.eightman.TestRunner.*;

/**
 * Integrations-Test mit einer vollständigen Beispiel-Akte.
 * Simuliert den gesamten Beratungsprozess ohne echte LLM-Aufrufe.
 */
public class SampleCaseIntegrationTest {

    public static void runAll() {
        testSampleCaseCreation();
        testSampleCaseJsonRoundtrip();
        testFullConsultationFlow();
        testResultJsonParsing();
    }

    /**
     * Erstellt die Beispiel-Akte:
     * "Einführung einer 4-Tage-Woche bei einem mittelständischen Softwareunternehmen"
     */
    private static Case createSampleCase() {
        Case c = new Case();
        c.setTitle("Einführung einer 4-Tage-Woche bei TechSoft GmbH");
        c.setDescription(
            "Die TechSoft GmbH (250 Mitarbeiter, Umsatz 35 Mio EUR) entwickelt " +
            "Enterprise-Software für den DACH-Raum. Die Geschäftsführung erwägt die " +
            "Einführung einer 4-Tage-Woche (32 Stunden bei vollem Gehalt) als Pilotprojekt " +
            "für die Entwicklungsabteilung (80 Personen). Hintergrund ist die zunehmende " +
            "Schwierigkeit, qualifizierte Entwickler zu rekrutieren. Die Fluktuation liegt " +
            "bei 18% p.a. und die durchschnittliche Time-to-Hire bei 4,5 Monaten."
        );
        c.setQuestion(
            "Sollte die TechSoft GmbH eine 4-Tage-Woche für die Entwicklungsabteilung " +
            "einführen, und wenn ja, unter welchen Bedingungen und mit welchem Zeitplan?"
        );
        c.setGoal(
            "Fundierte Entscheidungsgrundlage für die Geschäftsführung mit klaren " +
            "Handlungsempfehlungen, Risikobewertung und Implementierungsvorschlag."
        );
        c.setCategory("Personal & Organisation");
        c.setPriority("Hoch");
        c.setIndustry("Software / IT-Dienstleistungen");
        c.setStakeholders(
            "Geschäftsführung, HR-Abteilung, Entwicklungsleitung, 80 Entwickler, " +
            "Betriebsrat, Bestandskunden (SLA-Verpflichtungen), Vertrieb"
        );
        c.setConstraints(
            "Bestehende SLA-Verpflichtungen mit 24/5 Support-Zeiten; " +
            "Tarifvertrag IGM mit 35-Stunden-Woche als Basis; " +
            "Laufende Kundenprojekte mit festen Deadlines (Q4 2026); " +
            "Betriebsvereinbarung erforderlich"
        );
        c.setTimeframe("Pilotstart Q1 2027, Evaluierung nach 6 Monaten");
        c.setBudget(
            "Direkte Mehrkosten ca. 800.000 EUR/Jahr (20% Kapazitätsverlust " +
            "kompensiert durch Neueinstellungen), Recruiting-Budget 200.000 EUR"
        );
        c.setBackground(
            "Benchmark-Studie: 65% der Unternehmen mit 4-Tage-Woche berichten " +
            "gleichbleibende oder höhere Produktivität. Mitarbeiterbefragung 2025: " +
            "78% befürworten das Modell. Hauptkonkurrent DevPro AG hat bereits " +
            "eine 4-Tage-Woche eingeführt und meldet 40% weniger Fluktuation."
        );
        return c;
    }

    static void testSampleCaseCreation() {
        Case c = createSampleCase();
        assertNotNull("Beispiel-Akte: ID generiert", c.getId());
        assertEqual("Beispiel-Akte: Kategorie", "Personal & Organisation", c.getCategory());
        assertEqual("Beispiel-Akte: Priorität", "Hoch", c.getPriority());
        assertContains("Beispiel-Akte: Branche", c.getIndustry(), "Software");
        assertContains("Beispiel-Akte: Stakeholder enthält Betriebsrat",
                c.getStakeholders(), "Betriebsrat");
        assertContains("Beispiel-Akte: Rahmenbedingungen enthält SLA",
                c.getConstraints(), "SLA");
        assertContains("Beispiel-Akte: Budget enthält 800.000",
                c.getBudget(), "800.000");
    }

    static void testSampleCaseJsonRoundtrip() {
        Case original = createSampleCase();
        String json = original.toJson();

        // JSON muss alle Felder enthalten
        assertContains("JSON: Titel", json, "TechSoft GmbH");
        assertContains("JSON: 4-Tage-Woche", json, "4-Tage-Woche");
        assertContains("JSON: Kategorie", json, "Personal & Organisation");
        assertContains("JSON: Betriebsrat", json, "Betriebsrat");

        // Rückwärts-Parsing
        Case parsed = Case.fromJson(json);
        assertEqual("Roundtrip: Titel", original.getTitle(), parsed.getTitle());
        assertEqual("Roundtrip: Kategorie", original.getCategory(), parsed.getCategory());
        assertEqual("Roundtrip: Priorität", original.getPriority(), parsed.getPriority());
        assertEqual("Roundtrip: Branche", original.getIndustry(), parsed.getIndustry());
        assertContains("Roundtrip: Stakeholder", parsed.getStakeholders(), "Betriebsrat");
        assertContains("Roundtrip: Constraints", parsed.getConstraints(), "SLA");
        assertContains("Roundtrip: Timeframe", parsed.getTimeframe(), "Q1 2027");
    }

    static void testFullConsultationFlow() {
        Case c = createSampleCase();
        PromptBuilder builder = new PromptBuilder();
        AgentGeneratorService gen = new AgentGeneratorService();

        // 1. Agenten generieren
        List<Agent> agents = gen.generateCouncil();
        assertEqual("Flow: 8 Agenten", 8, agents.size());

        // 2. Phase 1: Analyse-Prompts generieren
        String analysisPrompt = builder.buildAnalysisPrompt(c);
        assertContains("Flow Analyse: enthält Titel", analysisPrompt, "TechSoft GmbH");
        assertContains("Flow Analyse: enthält Kategorie", analysisPrompt, "Personal & Organisation");
        assertContains("Flow Analyse: enthält Stakeholder", analysisPrompt, "Betriebsrat");
        assertContains("Flow Analyse: enthält Rahmenbedingungen", analysisPrompt, "SLA");
        assertContains("Flow Analyse: enthält Budget", analysisPrompt, "800.000");
        assertContains("Flow Analyse: enthält Hintergrund", analysisPrompt, "Benchmark");

        // System-Prompts für verschiedene Agententypen
        Agent consensusAgent = null;
        Agent eighthMan = null;
        for (Agent a : agents) {
            if (a.isEighthMan()) eighthMan = a;
            else if (consensusAgent == null) consensusAgent = a;
        }

        String sysPromptConsensus = builder.buildSystemPrompt(consensusAgent);
        String sysPromptEighth = builder.buildSystemPrompt(eighthMan);

        assertContains("Flow: Konsens-Agent hat Rolle", sysPromptConsensus, consensusAgent.getRole());
        assertContains("Flow: 8. Mann hat Achter-Mann-Hinweis", sysPromptEighth, "Achte Mann");

        // 3. Simulierte Analyse-Responses
        List<AgentResponse> analysisResponses = new ArrayList<>();
        for (Agent a : agents) {
            String simContent = "Analyse von " + a.getName() + ": Die 4-Tage-Woche " +
                    (a.isEighthMan() ? "birgt erhebliche Risiken." : "bietet Chancen für die Rekrutierung.");
            analysisResponses.add(new AgentResponse(a, simContent));
        }

        // 4. Phase 2: Konsens-Prompts
        String consensusPrompt = builder.buildConsensusPrompt(analysisResponses, false);
        assertContains("Flow Konsens: enthält Agenten-Analysen", consensusPrompt, "Analyse von");
        assertContains("Flow Konsens: enthält Konsensvorschlag-Aufforderung", consensusPrompt, "Konsensvorschlag");

        String eighthManConsensusPrompt = builder.buildConsensusPrompt(analysisResponses, true);
        assertContains("Flow 8Mann-Konsens: Groupthink-Warnung", eighthManConsensusPrompt, "Gruppendenken");

        // 5. Phase 3: Dissens-Prompt
        List<AgentResponse> consensusResponses = new ArrayList<>();
        for (Agent a : agents) {
            consensusResponses.add(new AgentResponse(a, "Konsens-Beitrag von " + a.getName()));
        }

        String dissentPrompt = builder.buildDissentPrompt(consensusResponses, true);
        assertContains("Flow Dissent: Worst-Case", dissentPrompt, "Worst-Case");
        assertContains("Flow Dissent: Alternative Hypothesen", dissentPrompt, "Alternative Hypothesen");

        // 6. Phase 4: Synthese
        ConsultationResult result = new ConsultationResult();
        result.addPhaseResult(createPhaseResult(Phase.ANALYSIS, agents, "Analyse"));
        result.addPhaseResult(createPhaseResult(Phase.CONSENSUS, agents, "Konsens"));
        result.addPhaseResult(createPhaseResult(Phase.DISSENT, agents, "Dissens"));

        String synthesisPrompt = builder.buildSynthesisPrompt(result, c);
        assertContains("Flow Synthese: enthält Falltitel", synthesisPrompt, "TechSoft");
        assertContains("Flow Synthese: JSON-Schema", synthesisPrompt, "proArguments");
        assertContains("Flow Synthese: alle Phasen", synthesisPrompt, "Analyse");

        assertTrue("Flow: Gesamter Prozess erfolgreich durchlaufen", true);
    }

    static void testResultJsonParsing() {
        // Simuliere eine typische LLM-Synthese-Antwort
        String llmResponse = "```json\n" +
            "{\n" +
            "  \"summary\": \"Die Einführung einer 4-Tage-Woche bei der TechSoft GmbH ist grundsätzlich empfehlenswert, " +
            "birgt jedoch Risiken bei der Einhaltung von SLA-Verpflichtungen. Ein stufenweiser Ansatz wird empfohlen.\",\n" +
            "  \"proArguments\": [\n" +
            "    \"Deutliche Verbesserung der Arbeitgeberattraktivität im hart umkämpften IT-Markt\",\n" +
            "    \"Reduzierung der Fluktuation (Benchmark: -40%)\",\n" +
            "    \"Höhere Mitarbeiterzufriedenheit (78% Zustimmung in interner Befragung)\",\n" +
            "    \"Wettbewerbsvorteil gegenüber Konkurrenz (DevPro AG als Vorbild)\"\n" +
            "  ],\n" +
            "  \"contraArguments\": [\n" +
            "    \"Kapazitätsverlust von 20% ohne Kompensation gefährdet Projektdeadlines\",\n" +
            "    \"Erhöhte Personalkosten von ca. 800.000 EUR/Jahr\",\n" +
            "    \"Risiko der Ungleichbehandlung zwischen Abteilungen\",\n" +
            "    \"Komplexe Anpassung der SLA-Vereinbarungen notwendig\"\n" +
            "  ],\n" +
            "  \"risks\": [\n" +
            "    \"SLA-Verletzungen bei kritischen Kundenprojekten\",\n" +
            "    \"Unzufriedenheit in nicht-betroffenen Abteilungen\",\n" +
            "    \"Schwierigkeit, zum alten Modell zurückzukehren\",\n" +
            "    \"Mögliche Produktivitätseinbußen in der Übergangsphase\"\n" +
            "  ],\n" +
            "  \"recommendations\": [\n" +
            "    \"Start als 6-monatiges Pilotprojekt mit einem Team (20 Entwickler)\",\n" +
            "    \"Klare KPIs definieren: Produktivität, Mitarbeiterzufriedenheit, Kundenzufriedenheit\",\n" +
            "    \"SLA-Absicherung durch gestaffelte Arbeitszeiten (Mo-Do + Do-So Rotation)\",\n" +
            "    \"Betriebsvereinbarung mit Rückkehrklausel verhandeln\",\n" +
            "    \"Begleitendes Change-Management für alle Abteilungen\"\n" +
            "  ],\n" +
            "  \"consensusScore\": 72,\n" +
            "  \"eighthManChallenge\": \"Die angenommene Produktivitätssteigerung basiert auf Studien aus anderen Branchen. " +
            "Software-Entwicklung mit komplexen Kontextwechseln könnte anders reagieren. Zudem werden die versteckten Kosten " +
            "der Koordination bei reduzierten Überlappungszeiten unterschätzt.\"\n" +
            "}\n" +
            "```";

        // Extrahiere JSON aus Markdown-Block
        String json = llmResponse;
        int jsonStart = llmResponse.indexOf("{");
        int jsonEnd = llmResponse.lastIndexOf("}");
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            json = llmResponse.substring(jsonStart, jsonEnd + 1);
        }

        Map<String, String> parsed = JsonHelper.parseJsonObject(json);
        assertNotNull("LLM-Antwort: summary vorhanden", parsed.get("summary"));
        assertContains("LLM-Antwort: summary Inhalt", parsed.get("summary"), "TechSoft");

        List<String> pros = JsonHelper.parseJsonArray(parsed.get("proArguments"));
        assertEqual("LLM-Antwort: 4 Pro-Argumente", 4, pros.size());
        assertContains("LLM-Antwort: Pro enthält Arbeitgeberattraktivität",
                pros.get(0), "Arbeitgeberattraktivität");

        List<String> contras = JsonHelper.parseJsonArray(parsed.get("contraArguments"));
        assertEqual("LLM-Antwort: 4 Contra-Argumente", 4, contras.size());

        List<String> risks = JsonHelper.parseJsonArray(parsed.get("risks"));
        assertEqual("LLM-Antwort: 4 Risiken", 4, risks.size());

        List<String> recs = JsonHelper.parseJsonArray(parsed.get("recommendations"));
        assertEqual("LLM-Antwort: 5 Empfehlungen", 5, recs.size());
        assertContains("LLM-Antwort: Pilotprojekt empfohlen", recs.get(0), "Pilotprojekt");

        assertEqual("LLM-Antwort: consensusScore", "72", parsed.get("consensusScore").trim());

        assertContains("LLM-Antwort: 8. Mann Challenge",
                parsed.get("eighthManChallenge"), "Produktivitätssteigerung");
    }

    private static PhaseResult createPhaseResult(Phase phase, List<Agent> agents, String prefix) {
        PhaseResult pr = new PhaseResult(phase);
        for (Agent a : agents) {
            pr.addResponse(new AgentResponse(a, prefix + "-Beitrag von " + a.getName()));
        }
        return pr;
    }
}
