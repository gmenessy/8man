package com.eightman;

import com.eightman.model.*;
import com.eightman.server.JsonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.eightman.TestRunner.*;

public class ConsultationResultTest {

    public static void runAll() {
        testDefaultConstructor();
        testSetFields();
        testToJsonStructure();
        testPhaseResultToJson();
        testConsultationStatusToJson();
        testConsultationStatusPhases();
    }

    static void testDefaultConstructor() {
        ConsultationResult r = new ConsultationResult();
        assertNotNull("Result: proArguments initialisiert", r.getProArguments());
        assertNotNull("Result: contraArguments initialisiert", r.getContraArguments());
        assertNotNull("Result: risks initialisiert", r.getRisks());
        assertNotNull("Result: recommendations initialisiert", r.getRecommendations());
        assertNotNull("Result: phaseResults initialisiert", r.getPhaseResults());
        assertTrue("Result: proArguments leer", r.getProArguments().isEmpty());
    }

    static void testSetFields() {
        ConsultationResult r = new ConsultationResult();
        r.setSummary("Zusammenfassung");
        r.setConsensusScore(75);
        r.setEighthManChallenge("Hauptkritik");

        List<String> pros = new ArrayList<>();
        pros.add("Pro 1");
        pros.add("Pro 2");
        r.setProArguments(pros);

        List<String> risks = new ArrayList<>();
        risks.add("Risiko 1");
        r.setRisks(risks);

        assertEqual("Result: summary", "Zusammenfassung", r.getSummary());
        assertEqual("Result: consensusScore", 75, r.getConsensusScore());
        assertEqual("Result: eighthManChallenge", "Hauptkritik", r.getEighthManChallenge());
        assertEqual("Result: proArguments Größe", 2, r.getProArguments().size());
        assertEqual("Result: risks Größe", 1, r.getRisks().size());
    }

    static void testToJsonStructure() {
        ConsultationResult r = new ConsultationResult();
        r.setSummary("Test Zusammenfassung");
        r.setConsensusScore(80);
        r.setEighthManChallenge("Hauptkritikpunkt");

        List<String> pros = new ArrayList<>();
        pros.add("Vorteil 1");
        r.setProArguments(pros);

        List<String> contras = new ArrayList<>();
        contras.add("Nachteil 1");
        r.setContraArguments(contras);

        String json = r.toJson();
        assertContains("Result JSON: summary", json, "Test Zusammenfassung");
        assertContains("Result JSON: consensusScore", json, "\"consensusScore\":80");
        assertContains("Result JSON: proArguments", json, "Vorteil 1");
        assertContains("Result JSON: contraArguments", json, "Nachteil 1");
        assertContains("Result JSON: eighthManChallenge", json, "Hauptkritikpunkt");
    }

    static void testPhaseResultToJson() {
        PhaseResult pr = new PhaseResult(Phase.ANALYSIS);
        Agent agent = new Agent("Test", "Rolle", "E", "S", "R", "A", "M", false);
        pr.addResponse(new AgentResponse(agent, "Inhalt der Analyse"));

        String json = pr.toJson();
        assertContains("PhaseResult JSON: phase", json, "ANALYSIS");
        assertContains("PhaseResult JSON: phaseName", json, "Analyse");
        assertContains("PhaseResult JSON: responses", json, "responses");
        assertContains("PhaseResult JSON: content", json, "Inhalt der Analyse");
    }

    static void testConsultationStatusToJson() {
        ConsultationStatus status = new ConsultationStatus("case-123");
        status.setCurrentPhase(Phase.CONSENSUS);
        status.setProgress(50);

        String json = status.toJson();
        assertContains("Status JSON: caseId", json, "case-123");
        assertContains("Status JSON: currentPhase", json, "CONSENSUS");
        assertContains("Status JSON: progress", json, "\"progress\":50");
        assertContains("Status JSON: complete false", json, "\"complete\":false");
    }

    static void testConsultationStatusPhases() {
        ConsultationStatus status = new ConsultationStatus("test");

        status.setCurrentPhase(Phase.ANALYSIS);
        assertEqual("Status Phase: ANALYSIS", Phase.ANALYSIS, status.getCurrentPhase());

        status.setCurrentPhase(Phase.DISSENT);
        assertEqual("Status Phase: DISSENT", Phase.DISSENT, status.getCurrentPhase());

        status.setComplete(true);
        assertTrue("Status: complete", status.isComplete());

        status.setFailed(true);
        status.setError("Testfehler");
        assertTrue("Status: failed", status.isFailed());
        assertEqual("Status: error", "Testfehler", status.getError());
    }
}
