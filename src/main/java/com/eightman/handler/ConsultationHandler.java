package com.eightman.handler;

import com.eightman.model.Case;
import com.eightman.model.ConsultationStatus;
import com.eightman.server.JsonHelper;
import com.eightman.service.ConsultationService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class ConsultationHandler implements HttpHandler {

    private final ConsultationService consultationService;
    private final Map<String, Case> cases;

    public ConsultationHandler(ConsultationService consultationService, Map<String, Case> cases) {
        this.consultationService = consultationService;
        this.cases = cases;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            String[] parts = path.split("/");
            if (parts.length < 5) {
                JsonHelper.sendError(exchange, 400, "Invalid path");
                return;
            }

            String caseId = parts[3];
            String action = parts[4];

            if ("consult".equals(action) && "POST".equalsIgnoreCase(method)) {
                startConsultation(exchange, caseId);
            } else if ("status".equals(action) && "GET".equalsIgnoreCase(method)) {
                getStatus(exchange, caseId);
            } else {
                JsonHelper.sendError(exchange, 405, "Method not allowed");
            }
        } catch (Exception e) {
            JsonHelper.sendError(exchange, 500, "Internal error: " + e.getMessage());
        }
    }

    private void startConsultation(HttpExchange exchange, String caseId) throws IOException {
        Case caseData = cases.get(caseId);
        if (caseData == null) {
            JsonHelper.sendError(exchange, 404, "Case not found");
            return;
        }

        ConsultationStatus existing = consultationService.getStatus(caseId);
        if (existing != null && !existing.isComplete() && !existing.isFailed()) {
            JsonHelper.sendError(exchange, 409, "Consultation already in progress");
            return;
        }

        ConsultationStatus status = consultationService.startConsultation(caseData);
        JsonHelper.sendJson(exchange, 202, status.toJson());
    }

    private void getStatus(HttpExchange exchange, String caseId) throws IOException {
        ConsultationStatus status = consultationService.getStatus(caseId);
        if (status == null) {
            JsonHelper.sendError(exchange, 404, "No consultation found for this case");
            return;
        }
        JsonHelper.sendJson(exchange, 200, status.toJson());
    }
}
