import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.example.Tarefa;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JavalinMainTest {
    private static Javalin app;
    private static final int PORT = 7000;
    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final List<Tarefa> tarefas = new ArrayList<>();

    @BeforeAll
    static void iniciarServidor() {
        app = Javalin.create().start(PORT);

        app.get("/hello", ctx -> ctx.result("Hello, Javalin!"));

        app.post("/tarefas", ctx -> {
            ObjectMapper mapper = new ObjectMapper();
            Tarefa nova = mapper.readValue(ctx.body(), Tarefa.class);
            nova.id = UUID.randomUUID().toString();
            tarefas.add(nova);
            ctx.status(201).json(nova);
        });

        app.get("/tarefas/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Tarefa encontrada = tarefas.stream()
                    .filter(t -> t.id.equals(id))
                    .findFirst()
                    .orElse(null);
            if (encontrada == null) {
                ctx.status(404).result("Tarefa não encontrada.");
            } else {
                ctx.json(encontrada);
            }
        });

        app.get("/tarefas", ctx -> {
            ctx.json(tarefas);
        });
    }

    @BeforeEach
    void limparLista() {
        tarefas.clear();
    }

    @AfterAll
    static void pararServidor() {
        app.stop();
    }

    @Test
    void deveRetornarStatus200() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/hello"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void deveRetornarMensagemHelloJavalin() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/hello"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Hello, Javalin!", response.body());
    }

    @Test
    void deveRetornarStatus201AoCriarNovaTarefa() throws IOException, InterruptedException {
        String json = """
                    {
                        "titulo": "Teste - Criar Nova Tarefa",
                        "descricao": "Teste com JUnit"
                    }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void deveBuscarItemRecemCadastrado() throws IOException, InterruptedException {
        String json = """
                    {
                        "titulo": "Tarefa Busca",
                        "descricao": "Utilizando path param"
                    }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        assertEquals(201, postResponse.statusCode(), "POST falhou, corpo: " + postResponse.body());
        System.out.println("POST response: " + postResponse.body());

        Tarefa tarefaCriada = mapper.readValue(postResponse.body(), Tarefa.class);

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas/" + tarefaCriada.id))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());

        Tarefa tarefaBuscada = mapper.readValue(getResponse.body(), Tarefa.class);

        assertEquals(tarefaCriada.id, tarefaBuscada.id);
        assertEquals(tarefaCriada.titulo, tarefaBuscada.titulo);
        assertEquals(tarefaCriada.descricao, tarefaBuscada.descricao);
    }

    @Test
    void deveRetornarListaDeTarefasNaoVazia() throws IOException, InterruptedException {
        String json = """
                    {
                        "titulo": "Lista de Tarefas",
                        "descricao": "array não vazio"
                    }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarefas"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        Tarefa[] lista = mapper.readValue(getResponse.body(), Tarefa[].class);

        assertTrue(lista.length > 0, "A lista de tarefas deveria conter pelo menos um item.");
    }
}

