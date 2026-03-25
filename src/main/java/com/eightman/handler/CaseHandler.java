package com.eightman.handler;

import com.eightman.model.Case;
import com.eightman.server.JsonHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaseHandler implements HttpHandler {

    private final Map<String, Case> cases = new ConcurrentHashMap<>();

    public Map<String, Case> getCases() {
        return cases;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.equals("/api/cases") || path.equals("/api/cases/")) {
                if ("POST".equalsIgnoreCase(method)) {
                    createCase(exchange);
                } else if ("GET".equalsIgnoreCase(method)) {
                    listCases(exchange);
                } else {
                    JsonHelper.sendError(exchange, 405, "Method not allowed");
                }
            } else {
                String id = path.substring("/api/cases/".length()).replaceAll("/.*", "");
                if ("GET".equalsIgnoreCase(method)) {
                    getCase(exchange, id);
                } else {
                    JsonHelper.sendError(exchange, 405, "Method not allowed");
                }
            }
        } catch (Exception e) {
            JsonHelper.sendError(exchange, 500, "Internal error: " + e.getMessage());
        }
    }

    private void createCase(HttpExchange exchange) throws IOException {
        String body = JsonHelper.readRequestBody(exchange);
        Case caseData = Case.fromJson(body);

        if (caseData.getTitle() == null || caseData.getQuestion() == null) {
            JsonHelper.sendError(exchange, 400, "Title and question are required");
            return;
        }

        cases.put(caseData.getId(), caseData);
        JsonHelper.sendJson(exchange, 201, caseData.toJson());
    }

    private void listCases(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Case c : cases.values()) {
            if (!first) sb.append(",");
            sb.append(c.toJson());
            first = false;
        }
        sb.append("]");
        JsonHelper.sendJson(exchange, 200, sb.toString());
    }

    private void getCase(HttpExchange exchange, String id) throws IOException {
        Case caseData = cases.get(id);
        if (caseData == null) {
            JsonHelper.sendError(exchange, 404, "Case not found");
            return;
        }
        JsonHelper.sendJson(exchange, 200, caseData.toJson());
    }
}
