package com.eightman.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StaticFileHandler implements HttpHandler {

    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html; charset=UTF-8");
        MIME_TYPES.put("css", "text/css; charset=UTF-8");
        MIME_TYPES.put("js", "application/javascript; charset=UTF-8");
        MIME_TYPES.put("json", "application/json; charset=UTF-8");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }

        String resourcePath = "/static" + path;
        InputStream is = getClass().getResourceAsStream(resourcePath);

        if (is == null) {
            // Try index.html for SPA-like routing
            is = getClass().getResourceAsStream("/static/index.html");
            if (is == null) {
                String msg = "404 Not Found";
                exchange.sendResponseHeaders(404, msg.length());
                exchange.getResponseBody().write(msg.getBytes(StandardCharsets.UTF_8));
                exchange.getResponseBody().close();
                return;
            }
            path = "/index.html";
        }

        String ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
        String contentType = MIME_TYPES.getOrDefault(ext, "application/octet-stream");
        exchange.getResponseHeaders().set("Content-Type", contentType);

        byte[] data = readAllBytes(is);
        exchange.sendResponseHeaders(200, data.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int len;
        while ((len = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, len);
        }
        is.close();
        return buffer.toByteArray();
    }
}
