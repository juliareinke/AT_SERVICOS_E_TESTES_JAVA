package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class ClienteStatusETimestamp {
    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = new URI("http://localhost:7000/status").toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int status = conn.getResponseCode();
        System.out.println("Código de status: " + status);

        InputStream respostaStream = (status < HttpURLConnection.HTTP_BAD_REQUEST)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(respostaStream));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> respostaMap = mapper.readValue(content.toString(), Map.class);

        System.out.println("Status: " + respostaMap.get("status"));
        System.out.println("Timestamp: " + respostaMap.get("timestamp"));
    }
}
