package com.eightman;

import com.eightman.model.Agent;
import com.eightman.service.AgentGeneratorService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.eightman.TestRunner.*;

public class AgentGeneratorTest {

    public static void runAll() {
        testGeneratesEightAgents();
        testExactlyOneEighthMan();
        testSevenConsensusAgents();
        testAllAgentsHaveRequiredFields();
        testEighthManHasSpecialRole();
        testUniqueAgentIds();
        testMultipleGenerationsVary();
    }

    static void testGeneratesEightAgents() {
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> agents = gen.generateCouncil();
        assertEqual("Council: genau 8 Agenten", 8, agents.size());
    }

    static void testExactlyOneEighthMan() {
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> agents = gen.generateCouncil();
        int count = 0;
        for (Agent a : agents) {
            if (a.isEighthMan()) count++;
        }
        assertEqual("Council: genau 1 Achter Mann", 1, count);
    }

    static void testSevenConsensusAgents() {
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> agents = gen.generateCouncil();
        int count = 0;
        for (Agent a : agents) {
            if (!a.isEighthMan()) count++;
        }
        assertEqual("Council: genau 7 Konsens-Agenten", 7, count);
    }

    static void testAllAgentsHaveRequiredFields() {
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> agents = gen.generateCouncil();

        for (Agent a : agents) {
            assertNotNull("Agent " + a.getName() + ": ID", a.getId());
            assertNotNull("Agent " + a.getName() + ": Name", a.getName());
            assertNotNull("Agent " + a.getName() + ": Rolle", a.getRole());
            assertNotNull("Agent " + a.getName() + ": Expertise", a.getExpertise());
            assertNotNull("Agent " + a.getName() + ": Denkstil", a.getThinkingStyle());
            assertNotNull("Agent " + a.getName() + ": Risikoneigung", a.getRiskTolerance());
            assertNotNull("Agent " + a.getName() + ": Argumentationsstil", a.getArgumentationStyle());
            assertNotNull("Agent " + a.getName() + ": Modell", a.getModelAssignment());
        }
    }

    static void testEighthManHasSpecialRole() {
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> agents = gen.generateCouncil();

        for (Agent a : agents) {
            if (a.isEighthMan()) {
                assertEqual("Achter Mann: Name", "Der Achte Mann", a.getName());
                assertEqual("Achter Mann: Rolle", "Systematischer Kritiker", a.getRole());
                assertContains("Achter Mann: Stil", a.getArgumentationStyle(), "Advocatus Diaboli");
                return;
            }
        }
        assertTrue("Achter Mann nicht gefunden", false);
    }

    static void testUniqueAgentIds() {
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> agents = gen.generateCouncil();
        Set<String> ids = new HashSet<>();
        for (Agent a : agents) {
            ids.add(a.getId());
        }
        assertEqual("Council: alle IDs unique", 8, ids.size());
    }

    static void testMultipleGenerationsVary() {
        // Da Agenten zufällig aus dem Pool gewählt werden, sollten zwei
        // Generierungen (meistens) unterschiedliche Zusammensetzungen haben
        AgentGeneratorService gen = new AgentGeneratorService();
        List<Agent> council1 = gen.generateCouncil();
        List<Agent> council2 = gen.generateCouncil();

        Set<String> names1 = new HashSet<>();
        Set<String> names2 = new HashSet<>();
        for (Agent a : council1) if (!a.isEighthMan()) names1.add(a.getName());
        for (Agent a : council2) if (!a.isEighthMan()) names2.add(a.getName());

        // Die Reihenfolge/Zusammensetzung kann variieren
        // Mindestens die IDs sollten unterschiedlich sein
        Set<String> ids1 = new HashSet<>();
        Set<String> ids2 = new HashSet<>();
        for (Agent a : council1) ids1.add(a.getId());
        for (Agent a : council2) ids2.add(a.getId());

        // IDs sind UUIDs, sollten nie gleich sein
        boolean anyOverlap = false;
        for (String id : ids1) {
            if (ids2.contains(id)) anyOverlap = true;
        }
        assertFalse("Zwei Councils: keine ID-Überlappung", anyOverlap);
    }
}
