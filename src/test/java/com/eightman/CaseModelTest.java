package com.eightman;

import com.eightman.model.Case;

import static com.eightman.TestRunner.*;

public class CaseModelTest {

    public static void runAll() {
        testDefaultConstructor();
        testParameterizedConstructor();
        testFromJsonBasic();
        testFromJsonExtended();
        testToJsonContainsAllFields();
        testToJsonRoundtrip();
        testFromJsonMissingOptionalFields();
        testSpecialCharactersInFields();
    }

    static void testDefaultConstructor() {
        Case c = new Case();
        assertNotNull("Case: ID wird generiert", c.getId());
        assertTrue("Case: ID ist nicht leer", !c.getId().isEmpty());
        assertTrue("Case: createdAt > 0", c.getCreatedAt() > 0);
    }

    static void testParameterizedConstructor() {
        Case c = new Case("Titel", "Beschreibung", "Frage?", "Ziel");
        assertEqual("Case: Titel", "Titel", c.getTitle());
        assertEqual("Case: Beschreibung", "Beschreibung", c.getDescription());
        assertEqual("Case: Frage", "Frage?", c.getQuestion());
        assertEqual("Case: Ziel", "Ziel", c.getGoal());
        assertNotNull("Case: ID vorhanden", c.getId());
    }

    static void testFromJsonBasic() {
        String json = "{\"title\":\"Markteintritt\",\"description\":\"Kontext\"," +
                "\"question\":\"Sollen wir expandieren?\",\"goal\":\"Entscheidung\"}";
        Case c = Case.fromJson(json);
        assertEqual("fromJson: title", "Markteintritt", c.getTitle());
        assertEqual("fromJson: description", "Kontext", c.getDescription());
        assertEqual("fromJson: question", "Sollen wir expandieren?", c.getQuestion());
        assertEqual("fromJson: goal", "Entscheidung", c.getGoal());
    }

    static void testFromJsonExtended() {
        String json = "{\"title\":\"Test\"," +
                "\"question\":\"Frage?\"," +
                "\"category\":\"Strategie\"," +
                "\"industry\":\"FinTech\"," +
                "\"stakeholders\":\"Vorstand, Kunden\"," +
                "\"constraints\":\"Regulierung BAFIN\"," +
                "\"timeframe\":\"Q3 2026\"," +
                "\"priority\":\"Hoch\"," +
                "\"budget\":\"500.000 EUR\"," +
                "\"background\":\"Marktanalyse liegt vor\"}";
        Case c = Case.fromJson(json);
        assertEqual("fromJson erweitert: category", "Strategie", c.getCategory());
        assertEqual("fromJson erweitert: industry", "FinTech", c.getIndustry());
        assertEqual("fromJson erweitert: stakeholders", "Vorstand, Kunden", c.getStakeholders());
        assertEqual("fromJson erweitert: constraints", "Regulierung BAFIN", c.getConstraints());
        assertEqual("fromJson erweitert: timeframe", "Q3 2026", c.getTimeframe());
        assertEqual("fromJson erweitert: priority", "Hoch", c.getPriority());
        assertEqual("fromJson erweitert: budget", "500.000 EUR", c.getBudget());
        assertEqual("fromJson erweitert: background", "Marktanalyse liegt vor", c.getBackground());
    }

    static void testToJsonContainsAllFields() {
        Case c = new Case("T", "D", "Q?", "G");
        c.setCategory("Technologie");
        c.setIndustry("Automotive");
        c.setStakeholders("Team");
        c.setConstraints("Budget begrenzt");
        c.setTimeframe("6 Monate");
        c.setPriority("Kritisch");
        c.setBudget("1 Mio");
        c.setBackground("Historie");

        String json = c.toJson();
        assertContains("toJson: enthält title", json, "\"title\"");
        assertContains("toJson: enthält category", json, "\"category\"");
        assertContains("toJson: enthält Technologie", json, "Technologie");
        assertContains("toJson: enthält industry", json, "\"industry\"");
        assertContains("toJson: enthält stakeholders", json, "\"stakeholders\"");
        assertContains("toJson: enthält constraints", json, "\"constraints\"");
        assertContains("toJson: enthält timeframe", json, "\"timeframe\"");
        assertContains("toJson: enthält priority", json, "\"priority\"");
        assertContains("toJson: enthält budget", json, "\"budget\"");
        assertContains("toJson: enthält background", json, "\"background\"");
    }

    static void testToJsonRoundtrip() {
        Case original = new Case("Roundtrip Test", "Beschreibung", "Frage?", "Ziel");
        original.setCategory("Finanzen");
        original.setPriority("Mittel");

        String json = original.toJson();
        // Parsen wir die wesentlichen Felder zurück
        Case parsed = Case.fromJson(json);
        assertEqual("Roundtrip: title", original.getTitle(), parsed.getTitle());
        assertEqual("Roundtrip: description", original.getDescription(), parsed.getDescription());
        assertEqual("Roundtrip: category", original.getCategory(), parsed.getCategory());
        assertEqual("Roundtrip: priority", original.getPriority(), parsed.getPriority());
    }

    static void testFromJsonMissingOptionalFields() {
        String json = "{\"title\":\"Minimal\",\"question\":\"Warum?\"}";
        Case c = Case.fromJson(json);
        assertEqual("fromJson minimal: title", "Minimal", c.getTitle());
        assertEqual("fromJson minimal: question", "Warum?", c.getQuestion());
        // Optionale Felder sollten null sein
        assertTrue("fromJson minimal: category ist null", c.getCategory() == null);
        assertTrue("fromJson minimal: industry ist null", c.getIndustry() == null);
        assertTrue("fromJson minimal: stakeholders ist null", c.getStakeholders() == null);
    }

    static void testSpecialCharactersInFields() {
        String json = "{\"title\":\"Test mit \\\"Anführungszeichen\\\" und Umlauten äöü\"," +
                "\"question\":\"Frage mit\\nZeilenumbruch\"}";
        Case c = Case.fromJson(json);
        assertContains("Sonderzeichen: Anführungszeichen", c.getTitle(), "\"Anführungszeichen\"");
        assertContains("Sonderzeichen: Umlaute", c.getTitle(), "äöü");
        assertContains("Sonderzeichen: Zeilenumbruch", c.getQuestion(), "\n");
    }
}
