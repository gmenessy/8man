package com.eightman;

import com.eightman.model.Agent;
import com.eightman.model.AgentResponse;
import com.eightman.server.JsonHelper;

import java.util.Map;

import static com.eightman.TestRunner.*;

public class AgentModelTest {

    public static void runAll() {
        testAgentCreation();
        testEighthManAgent();
        testAgentToJson();
        testAgentResponseCreation();
        testAgentResponseToJson();
        testAgentResponseEighthManFlag();
    }

    static void testAgentCreation() {
        Agent a = new Agent("Dr. Voss", "Strategin", "Planung", "analytisch",
                "mittel", "datengetrieben", "gpt-4o", false);
        assertEqual("Agent: Name", "Dr. Voss", a.getName());
        assertEqual("Agent: Rolle", "Strategin", a.getRole());
        assertEqual("Agent: Expertise", "Planung", a.getExpertise());
        assertEqual("Agent: Denkstil", "analytisch", a.getThinkingStyle());
        assertEqual("Agent: Risikoneigung", "mittel", a.getRiskTolerance());
        assertEqual("Agent: Argumentationsstil", "datengetrieben", a.getArgumentationStyle());
        assertEqual("Agent: Modell", "gpt-4o", a.getModelAssignment());
        assertFalse("Agent: ist kein Achter Mann", a.isEighthMan());
        assertNotNull("Agent: ID generiert", a.getId());
    }

    static void testEighthManAgent() {
        Agent a = new Agent("Der Achte Mann", "Kritiker", "Gegenargumente",
                "konträr", "entgegengesetzt", "Advocatus Diaboli", "gpt-4o", true);
        assertTrue("Achter Mann: Flag ist true", a.isEighthMan());
        assertEqual("Achter Mann: Rolle", "Kritiker", a.getRole());
    }

    static void testAgentToJson() {
        Agent a = new Agent("Test", "Rolle", "Expertise", "Stil",
                "hoch", "kritisch", "model-1", false);
        String json = a.toJson();
        assertContains("Agent toJson: name", json, "\"name\":\"Test\"");
        assertContains("Agent toJson: role", json, "\"role\":\"Rolle\"");
        assertContains("Agent toJson: eighthMan false", json, "\"eighthMan\":false");

        // Prüfe, dass JSON parsbar ist
        Map<String, String> map = JsonHelper.parseJsonObject(json);
        assertEqual("Agent toJson parsbar: name", "Test", map.get("name"));
    }

    static void testAgentResponseCreation() {
        Agent a = new Agent("Analyst", "Finanzanalyst", "Finanzen", "systematisch",
                "konservativ", "evidenzbasiert", "gpt-4o", false);
        AgentResponse resp = new AgentResponse(a, "Meine Analyse lautet...");
        assertEqual("AgentResponse: Name", "Analyst", resp.getAgentName());
        assertEqual("AgentResponse: Rolle", "Finanzanalyst", resp.getAgentRole());
        assertEqual("AgentResponse: Content", "Meine Analyse lautet...", resp.getContent());
        assertFalse("AgentResponse: nicht Achter Mann", resp.isEighthMan());
    }

    static void testAgentResponseToJson() {
        Agent a = new Agent("Test", "Rolle", "E", "S", "R", "A", "M", true);
        AgentResponse resp = new AgentResponse(a, "Kritik: Dies ist problematisch.");
        String json = resp.toJson();
        assertContains("AgentResponse toJson: agentName", json, "\"agentName\":\"Test\"");
        assertContains("AgentResponse toJson: eighthMan true", json, "\"eighthMan\":true");
        assertContains("AgentResponse toJson: content", json, "Kritik: Dies ist problematisch.");
    }

    static void testAgentResponseEighthManFlag() {
        Agent consensus = new Agent("A", "R", "E", "S", "R", "A", "M", false);
        Agent dissenter = new Agent("B", "R", "E", "S", "R", "A", "M", true);

        AgentResponse r1 = new AgentResponse(consensus, "ok");
        AgentResponse r2 = new AgentResponse(dissenter, "nein");

        assertFalse("Response: Konsens-Agent", r1.isEighthMan());
        assertTrue("Response: Dissens-Agent", r2.isEighthMan());
    }
}
