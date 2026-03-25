package com.eightman;

import com.eightman.config.AppConfig;
import com.eightman.handler.CaseHandler;
import com.eightman.handler.ConsultationHandler;
import com.eightman.handler.StaticFileHandler;
import com.eightman.server.CorsHandler;
import com.eightman.service.ConsultationService;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.getInstance();
        int port = config.getServerPort();

        if (config.getApiKey().isEmpty()) {
            LOGGER.warning("EIGHTMAN_API_KEY is not set! LLM calls will fail.");
            LOGGER.info("Set environment variable: export EIGHTMAN_API_KEY=your-api-key");
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        CaseHandler caseHandler = new CaseHandler();
        ConsultationService consultationService = new ConsultationService();
        ConsultationHandler consultationHandler = new ConsultationHandler(
                consultationService, caseHandler.getCases());

        // API routes
        server.createContext("/api/cases", new CorsHandler(exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.contains("/consult") || path.contains("/status")) {
                consultationHandler.handle(exchange);
            } else {
                caseHandler.handle(exchange);
            }
        }));

        // Static files (frontend)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();

        LOGGER.info("===========================================");
        LOGGER.info("  8man Expert Council Simulator");
        LOGGER.info("  Server running on http://localhost:" + port);
        LOGGER.info("  API Base URL: " + config.getApiBaseUrl());
        LOGGER.info("  Default Model: " + config.getDefaultModel());
        LOGGER.info("===========================================");
    }
}
