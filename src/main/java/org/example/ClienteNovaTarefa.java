package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClienteNovaTarefa {
    public static void main(String[] args) {
        try {
            URL url = new URI("http://localhost:7000/tarefas").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInput = """
                    {
                        "titulo": "Assessment de Java",
                        "descricao": "Javalin, JUnit e HttpURLConnection"
                    }
                    """;

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            int status = conn.getResponseCode();
            System.out.println("Código de resposta: " + status);

            InputStream respostaStream = (status < HttpURLConnection.HTTP_BAD_REQUEST)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String resposta = new String(respostaStream.readAllBytes());
            System.out.println("Resposta da API: " + resposta);

            conn.disconnect();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
