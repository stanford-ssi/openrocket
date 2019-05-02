package net.sf.openrocket.startup;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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

    static private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();

        if (query == null) {
            return result;
        }

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private static double extractParam(Map<String, String> params, String name, double defaultValue) {
        if (params.containsKey(name)) {
            double result = Double.parseDouble(params.get(name));
            if (!Double.isNaN(result)) {
                return result;
            }
        }

        return defaultValue;
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
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());

                int sampleEvery = 1;
                double spinRate = extractParam(params, "spin", 0);
                double launchAltitude = extractParam(params, "altitude", 0);
                double launchLatitude = extractParam(params, "latitude", 36);
                double launchLongitude = extractParam(params, "longitude", -121);

                if (params.containsKey("sample")) {
                    sampleEvery = Integer.parseInt(params.get("sample"));
                    if (sampleEvery < 1) {
                        sampleEvery = 1;
                    }
                }


                writeResponse(HeadlessRockoon.generateServerResponse(sampleEvery, spinRate, launchAltitude, launchLatitude, launchLongitude), httpExchange);
            } catch (Exception e) {
                e.printStackTrace();
                writeResponse("Internal server error", httpExchange, 500);
            }
        }
    }
}
