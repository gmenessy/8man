package com.eightman;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimales Test-Framework ohne externe Abhängigkeiten.
 * Führt alle Testsuiten aus und gibt einen strukturierten Bericht aus.
 */
public class TestRunner {

    private static int totalTests = 0;
    private static int passed = 0;
    private static int failed = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  8man Test Suite");
        System.out.println("==============================================\n");

        long start = System.currentTimeMillis();

        // Alle Testklassen ausführen
        runSuite("JsonHelper Tests", JsonHelperTest::runAll);
        runSuite("Case Model Tests", CaseModelTest::runAll);
        runSuite("Agent Model Tests", AgentModelTest::runAll);
        runSuite("AgentGenerator Tests", AgentGeneratorTest::runAll);
        runSuite("PromptBuilder Tests", PromptBuilderTest::runAll);
        runSuite("ConsultationResult Tests", ConsultationResultTest::runAll);
        runSuite("Phase Tests", PhaseTest::runAll);
        runSuite("Beispiel-Akte Integration", SampleCaseIntegrationTest::runAll);

        long elapsed = System.currentTimeMillis() - start;

        // Ergebnisbericht
        System.out.println("\n==============================================");
        System.out.println("  ERGEBNIS");
        System.out.println("==============================================");
        System.out.println("  Gesamt:      " + totalTests);
        System.out.println("  Bestanden:   " + passed);
        System.out.println("  Fehlgeschlagen: " + failed);
        System.out.println("  Dauer:       " + elapsed + "ms");

        if (!failures.isEmpty()) {
            System.out.println("\n  FEHLGESCHLAGENE TESTS:");
            for (String f : failures) {
                System.out.println("    - " + f);
            }
        }

        System.out.println("==============================================");
        System.exit(failed > 0 ? 1 : 0);
    }

    private static void runSuite(String name, Runnable suite) {
        System.out.println("--- " + name + " ---");
        try {
            suite.run();
        } catch (Exception e) {
            System.out.println("  [SUITE FEHLER] " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // === Assertion-Hilfsmethoden ===

    public static void assertEqual(String testName, Object expected, Object actual) {
        totalTests++;
        if (expected == null && actual == null) {
            passed++;
            System.out.println("  [OK] " + testName);
        } else if (expected != null && expected.equals(actual)) {
            passed++;
            System.out.println("  [OK] " + testName);
        } else {
            failed++;
            String msg = testName + " | Erwartet: " + expected + " | Tatsächlich: " + actual;
            failures.add(msg);
            System.out.println("  [FAIL] " + msg);
        }
    }

    public static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passed++;
            System.out.println("  [OK] " + testName);
        } else {
            failed++;
            failures.add(testName + " | Bedingung war false");
            System.out.println("  [FAIL] " + testName + " | Bedingung war false");
        }
    }

    public static void assertFalse(String testName, boolean condition) {
        assertTrue(testName, !condition);
    }

    public static void assertNotNull(String testName, Object obj) {
        totalTests++;
        if (obj != null) {
            passed++;
            System.out.println("  [OK] " + testName);
        } else {
            failed++;
            failures.add(testName + " | Objekt war null");
            System.out.println("  [FAIL] " + testName + " | Objekt war null");
        }
    }

    public static void assertContains(String testName, String haystack, String needle) {
        totalTests++;
        if (haystack != null && haystack.contains(needle)) {
            passed++;
            System.out.println("  [OK] " + testName);
        } else {
            failed++;
            String msg = testName + " | '" + needle + "' nicht gefunden in Text";
            failures.add(msg);
            System.out.println("  [FAIL] " + msg);
        }
    }
}
