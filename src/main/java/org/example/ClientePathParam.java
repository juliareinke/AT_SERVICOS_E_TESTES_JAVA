package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ClientePathParam {
    public static void main(String[] args) throws IOException, URISyntaxException {
        // Id varia conforme a tarefa criada por ser UUID
        String idDaTarefa = "1cbdf287-76e9-4ecc-a01d-679e66424637";

        URL url = new URI("http://localhost:7000/tarefas/" + idDaTarefa).toURL();
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

        System.out.println("Resposta:");
        System.out.println(content);
    }
}
