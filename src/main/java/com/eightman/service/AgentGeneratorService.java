package com.eightman.service;

import com.eightman.config.AppConfig;
import com.eightman.model.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AgentGeneratorService {

    private static final List<AgentTemplate> TEMPLATES = Arrays.asList(
        new AgentTemplate("Dr. Elena Voss", "Strategin", "Strategische Planung & Unternehmensführung",
                "analytisch", "mittel", "datengetrieben"),
        new AgentTemplate("Prof. Marcus Chen", "Finanzanalyst", "Finanzanalyse & Risikobewertung",
                "systematisch", "konservativ", "evidenzbasiert"),
        new AgentTemplate("Dr. Sarah Okonkwo", "Rechtsexpertin", "Recht & Regulierung",
                "präzise", "konservativ", "argumentativ"),
        new AgentTemplate("Ing. Raj Patel", "Technologieführer", "Technologie & Innovation",
                "kreativ", "risikofreudig", "visionär"),
        new AgentTemplate("Dr. Anna Bergström", "Ethikberaterin", "Ethik & Gesellschaftliche Verantwortung",
                "reflektierend", "konservativ", "sokratisch"),
        new AgentTemplate("Max Thornton", "Operationsmanager", "Betrieb & Prozessoptimierung",
                "pragmatisch", "mittel", "ergebnisorientiert"),
        new AgentTemplate("Dr. Yuki Tanaka", "Marktforscherin", "Marktanalyse & Verbraucherverhalten",
                "intuitiv", "mittel", "narrativ"),
        new AgentTemplate("Prof. David Müller", "Risikoanalyst", "Risikomanagement & Szenarioplanung",
                "skeptisch", "konservativ", "kritisch"),
        new AgentTemplate("Dr. Lisa Park", "Innovationsdirektorin", "Innovationsmanagement & Disruption",
                "visionär", "risikofreudig", "inspirierend"),
        new AgentTemplate("James O'Brien", "Kundenanwalt", "Kundenerfahrung & Stakeholder-Management",
                "empathisch", "mittel", "narrativ")
    );

    public List<Agent> generateCouncil() {
        String defaultModel = AppConfig.getInstance().getDefaultModel();
        List<AgentTemplate> pool = new ArrayList<>(TEMPLATES);
        Collections.shuffle(pool);

        List<Agent> agents = new ArrayList<>();

        // 7 consensus agents
        for (int i = 0; i < 7 && i < pool.size(); i++) {
            AgentTemplate t = pool.get(i);
            agents.add(new Agent(t.name, t.role, t.expertise, t.thinkingStyle,
                    t.riskTolerance, t.argumentationStyle, defaultModel, false));
        }

        // 1 Eighth Man (dissenter)
        agents.add(new Agent(
                "Der Achte Mann", "Systematischer Kritiker",
                "Kritische Analyse & Gegenargumente",
                "konträr", "entgegengesetzt zur Mehrheit",
                "Advocatus Diaboli",
                defaultModel, true
        ));

        return agents;
    }

    private static class AgentTemplate {
        final String name, role, expertise, thinkingStyle, riskTolerance, argumentationStyle;

        AgentTemplate(String name, String role, String expertise, String thinkingStyle,
                      String riskTolerance, String argumentationStyle) {
            this.name = name;
            this.role = role;
            this.expertise = expertise;
            this.thinkingStyle = thinkingStyle;
            this.riskTolerance = riskTolerance;
            this.argumentationStyle = argumentationStyle;
        }
    }
}
