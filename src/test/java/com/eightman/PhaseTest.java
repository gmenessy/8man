package com.eightman;

import com.eightman.model.Phase;

import static com.eightman.TestRunner.*;

public class PhaseTest {

    public static void runAll() {
        testPhaseValues();
        testPhaseDisplayNames();
        testPhaseCount();
    }

    static void testPhaseValues() {
        assertNotNull("Phase: ANALYSIS existiert", Phase.ANALYSIS);
        assertNotNull("Phase: CONSENSUS existiert", Phase.CONSENSUS);
        assertNotNull("Phase: DISSENT existiert", Phase.DISSENT);
        assertNotNull("Phase: SYNTHESIS existiert", Phase.SYNTHESIS);
    }

    static void testPhaseDisplayNames() {
        assertEqual("Phase Anzeigename: ANALYSIS", "Analyse", Phase.ANALYSIS.getDisplayName());
        assertEqual("Phase Anzeigename: CONSENSUS", "Konsensbildung", Phase.CONSENSUS.getDisplayName());
        assertContains("Phase Anzeigename: DISSENT", Phase.DISSENT.getDisplayName(), "Dissens");
        assertEqual("Phase Anzeigename: SYNTHESIS", "Synthese", Phase.SYNTHESIS.getDisplayName());
    }

    static void testPhaseCount() {
        assertEqual("Phase: genau 4 Phasen", 4, Phase.values().length);
    }
}
