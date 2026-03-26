package com.eightman;

import com.eightman.server.JsonHelper;
import java.util.List;
import java.util.Map;

import static com.eightman.TestRunner.*;

public class JsonHelperTest {

    public static void runAll() {
        testEscapeJsonString();
        testEscapeSpecialChars();
        testEscapeNull();
        testParseSimpleObject();
        testParseObjectWithSpecialChars();
        testParseNestedObject();
        testParseEmptyObject();
        testParseSimpleArray();
        testParseStringArray();
        testParseEmptyArray();
        testListToJsonArray();
        testListToJsonArrayEmpty();
        testRoundtripObjectParsing();
    }

    static void testEscapeJsonString() {
        String result = JsonHelper.escapeJsonString("Hallo Welt");
        assertEqual("escapeJsonString: normaler Text", "\"Hallo Welt\"", result);
    }

    static void testEscapeSpecialChars() {
        String result = JsonHelper.escapeJsonString("Zeile1\nZeile2\t\"Tab\"");
        assertEqual("escapeJsonString: Sonderzeichen",
                "\"Zeile1\\nZeile2\\t\\\"Tab\\\"\"", result);
    }

    static void testEscapeNull() {
        String result = JsonHelper.escapeJsonString(null);
        assertEqual("escapeJsonString: null", "null", result);
    }

    static void testParseSimpleObject() {
        String json = "{\"title\":\"Test\",\"question\":\"Warum?\"}";
        Map<String, String> map = JsonHelper.parseJsonObject(json);
        assertEqual("parseJsonObject: title", "Test", map.get("title"));
        assertEqual("parseJsonObject: question", "Warum?", map.get("question"));
    }

    static void testParseObjectWithSpecialChars() {
        String json = "{\"text\":\"Zeile1\\nZeile2\",\"quote\":\"Er sagte \\\"ja\\\"\"}";
        Map<String, String> map = JsonHelper.parseJsonObject(json);
        assertEqual("parseJsonObject: Newline", "Zeile1\nZeile2", map.get("text"));
        assertEqual("parseJsonObject: Anführungszeichen", "Er sagte \"ja\"", map.get("quote"));
    }

    static void testParseNestedObject() {
        String json = "{\"name\":\"Test\",\"nested\":{\"a\":\"1\",\"b\":\"2\"},\"after\":\"ok\"}";
        Map<String, String> map = JsonHelper.parseJsonObject(json);
        assertEqual("parseJsonObject: name", "Test", map.get("name"));
        assertNotNull("parseJsonObject: nested vorhanden", map.get("nested"));
        assertEqual("parseJsonObject: after", "ok", map.get("after"));
    }

    static void testParseEmptyObject() {
        Map<String, String> map = JsonHelper.parseJsonObject("{}");
        assertTrue("parseJsonObject: leeres Objekt", map.isEmpty());
    }

    static void testParseSimpleArray() {
        String json = "[\"Eins\",\"Zwei\",\"Drei\"]";
        List<String> list = JsonHelper.parseJsonArray(json);
        assertEqual("parseJsonArray: Größe", 3, list.size());
        assertEqual("parseJsonArray: Element 0", "Eins", list.get(0));
        assertEqual("parseJsonArray: Element 1", "Zwei", list.get(1));
        assertEqual("parseJsonArray: Element 2", "Drei", list.get(2));
    }

    static void testParseStringArray() {
        String json = "[\"Risiko: Marktvolatilität\",\"Risiko: Regulierung\"]";
        List<String> list = JsonHelper.parseJsonArray(json);
        assertEqual("parseJsonArray: Risiko-Element", "Risiko: Marktvolatilität", list.get(0));
    }

    static void testParseEmptyArray() {
        List<String> list = JsonHelper.parseJsonArray("[]");
        assertTrue("parseJsonArray: leeres Array", list.isEmpty());
    }

    static void testListToJsonArray() {
        List<String> items = new java.util.ArrayList<>();
        items.add("Pro 1");
        items.add("Pro 2");
        String result = JsonHelper.listToJsonArray(items);
        assertContains("listToJsonArray: enthält Pro 1", result, "\"Pro 1\"");
        assertContains("listToJsonArray: enthält Pro 2", result, "\"Pro 2\"");
        assertTrue("listToJsonArray: beginnt mit [", result.startsWith("["));
        assertTrue("listToJsonArray: endet mit ]", result.endsWith("]"));
    }

    static void testListToJsonArrayEmpty() {
        String result = JsonHelper.listToJsonArray(new java.util.ArrayList<>());
        assertEqual("listToJsonArray: leere Liste", "[]", result);
    }

    static void testRoundtripObjectParsing() {
        // Baue JSON, parse es, prüfe Werte
        String json = "{\"summary\":\"Dies ist eine Zusammenfassung mit Umlauten: äöü\"," +
                "\"score\":\"75\",\"items\":[\"A\",\"B\"]}";
        Map<String, String> map = JsonHelper.parseJsonObject(json);
        assertEqual("Roundtrip: summary Umlaute",
                "Dies ist eine Zusammenfassung mit Umlauten: äöü", map.get("summary"));
        assertEqual("Roundtrip: score", "75", map.get("score"));

        List<String> items = JsonHelper.parseJsonArray(map.get("items"));
        assertEqual("Roundtrip: items Größe", 2, items.size());
        assertEqual("Roundtrip: items[0]", "A", items.get(0));
    }
}
