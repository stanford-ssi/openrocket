package net.sf.openrocket.startup;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class RockoonServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/", new SimpleHandler());
        server.createContext("/predict", new PredictionHandler());

        server.setExecutor(null);
        server.start();
    }

    static private void writeResponse(String response, HttpExchange httpExchange, int statusCode) throws IOException {
        byte[] data = response.getBytes("UTF-8");
        httpExchange.sendResponseHeaders(statusCode, data.length);

        try (BufferedOutputStream out = new BufferedOutputStream(httpExchange.getResponseBody())) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
                byte [] buffer = new byte [1024];
                int count ;
                while ((count = bis.read(buffer)) != -1) {
                    out.write(buffer, 0, count);
                }
            }
        }
    }

    static private void writeResponse(String response, HttpExchange t) throws IOException {
        writeResponse(response, t, 200);
    }

    static class SimpleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "{\"up\": true}";
            writeResponse(response, t);
        }
    }

    static class PredictionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                writeResponse(HeadlessRockoon.generateServerResponse(), t);
            } catch (Exception e) {
                e.printStackTrace();
                writeResponse("Internal server error", t, 500);
            }
        }
    }
}
