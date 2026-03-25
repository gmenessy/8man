package com.eightman.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Minimal JSON helper without external dependencies.
 * Handles simple JSON parsing and generation for the application's data structures.
 */
public class JsonHelper {

    public static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = "{\"error\":" + escapeJsonString(message) + "}";
        sendJson(exchange, statusCode, json);
    }

    public static Map<String, String> parseJsonObject(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            // Find key
            int keyStart = json.indexOf('"', i);
            if (keyStart < 0) break;
            int keyEnd = findClosingQuote(json, keyStart + 1);
            if (keyEnd < 0) break;
            String key = unescapeJsonString(json.substring(keyStart + 1, keyEnd));

            // Find colon
            int colon = json.indexOf(':', keyEnd + 1);
            if (colon < 0) break;

            // Find value
            int valStart = colon + 1;
            while (valStart < json.length() && json.charAt(valStart) == ' ') valStart++;

            if (valStart >= json.length()) break;

            char c = json.charAt(valStart);
            String value;
            int valEnd;

            if (c == '"') {
                valEnd = findClosingQuote(json, valStart + 1);
                if (valEnd < 0) break;
                value = unescapeJsonString(json.substring(valStart + 1, valEnd));
                i = valEnd + 1;
            } else if (c == '[') {
                valEnd = findClosingBracket(json, valStart, '[', ']');
                value = json.substring(valStart, valEnd + 1).trim();
                i = valEnd + 1;
            } else if (c == '{') {
                valEnd = findClosingBracket(json, valStart, '{', '}');
                value = json.substring(valStart, valEnd + 1).trim();
                i = valEnd + 1;
            } else {
                // number, boolean, null
                valEnd = valStart;
                while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}') {
                    valEnd++;
                }
                value = json.substring(valStart, valEnd).trim();
                i = valEnd;
            }

            map.put(key, value);

            // Skip comma
            while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) == ' ' || json.charAt(i) == '\n' || json.charAt(i) == '\r')) {
                i++;
            }
        }
        return map;
    }

    public static List<String> parseJsonArray(String json) {
        List<String> list = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return list;
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        json = json.trim();
        if (json.isEmpty()) return list;

        int i = 0;
        while (i < json.length()) {
            while (i < json.length() && (json.charAt(i) == ' ' || json.charAt(i) == '\n' || json.charAt(i) == '\r')) i++;
            if (i >= json.length()) break;

            char c = json.charAt(i);
            if (c == '"') {
                int end = findClosingQuote(json, i + 1);
                if (end < 0) break;
                list.add(unescapeJsonString(json.substring(i + 1, end)));
                i = end + 1;
            } else if (c == '{') {
                int end = findClosingBracket(json, i, '{', '}');
                list.add(json.substring(i, end + 1));
                i = end + 1;
            } else if (c == '[') {
                int end = findClosingBracket(json, i, '[', ']');
                list.add(json.substring(i, end + 1));
                i = end + 1;
            } else if (c == ',') {
                i++;
            } else {
                int end = i;
                while (end < json.length() && json.charAt(end) != ',') end++;
                list.add(json.substring(i, end).trim());
                i = end;
            }

            while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) == ' ' || json.charAt(i) == '\n' || json.charAt(i) == '\r')) {
                i++;
            }
        }
        return list;
    }

    public static String escapeJsonString(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private static String unescapeJsonString(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"': sb.append('"'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case 'b': sb.append('\b'); i++; break;
                    case 'f': sb.append('\f'); i++; break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            String hex = s.substring(i + 2, i + 6);
                            sb.append((char) Integer.parseInt(hex, 16));
                            i += 5;
                        }
                        break;
                    default: sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int findClosingQuote(String s, int start) {
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '\\') {
                i++; // skip escaped char
            } else if (s.charAt(i) == '"') {
                return i;
            }
        }
        return -1;
    }

    private static int findClosingBracket(String s, int start, char open, char close) {
        int depth = 0;
        boolean inString = false;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && inString) {
                i++;
                continue;
            }
            if (c == '"') {
                inString = !inString;
            } else if (!inString) {
                if (c == open) depth++;
                else if (c == close) {
                    depth--;
                    if (depth == 0) return i;
                }
            }
        }
        return s.length() - 1;
    }

    public static String listToJsonArray(List<String> items) {
        if (items == null || items.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeJsonString(items.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
}
