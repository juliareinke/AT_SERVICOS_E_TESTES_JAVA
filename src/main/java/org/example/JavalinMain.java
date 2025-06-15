package org.example;

import io.javalin.Javalin;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavalinMain {
    public static void main(String[] args) {
        // Criando o servidor com Javalin e definindo o charset UTF-8
        Javalin app = Javalin.create(
                javalinConfig -> javalinConfig.http.defaultContentType = "text/plain; charset=UTF-8"
        ).start(7000);

        // Definindo o endpoint /hello
        app.get("/hello", ctx -> {
            ctx.result("Hello, Javalin!");
        });

        // Endpoint /status -> retorna status e timestamp formatado
        app.get("/status", ctx -> {
            String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            ctx.json(Map.of(
                    "status", "ok",
                    "timestamp", timestamp
            ));
        });

        // Endpoint /echo -> post de mensagem
        app.post("/echo", ctx -> {
            Mensagem mensagem = ctx.bodyAsClass(Mensagem.class);
            ctx.json(mensagem);
        });

        // Endpoint com Path Parameter /saudacao/{nome}
        app.get("/saudacao/{nome}", ctx -> {
            String nome = ctx.pathParam("nome");
            ctx.json(new Mensagem("Olá, " + nome + "!"));
        });

        // Endpoint /tarefas para criar as tarefas
        app.post("/tarefas", ctx -> {
            Tarefa provisoria = ctx.bodyAsClass(Tarefa.class);

            // Definindo título como obrigatório e fazendo validação
            if (provisoria.titulo.isBlank() || provisoria.titulo == null) {
                ctx.status(400).result("Título é obrigatório.");
                return;
            }

            Tarefa definitiva = new Tarefa(provisoria.titulo, provisoria.descricao);
            listaTarefas.add(definitiva);

            ctx.status(201).json(definitiva);
        });

        // Endpoint /tarefas para acessar as tarefas
        app.get("/tarefas", ctx -> {
            ctx.json(listaTarefas);
        });

        // Endpoint com Path Param para buscar tarefas por id (/tarefas/{id})
        app.get("/tarefas/{id}", ctx -> {
            String id = ctx.pathParam("id");

            Tarefa retornada = listaTarefas.stream()
                    .filter(t -> t.id.equals(id))
                    .findFirst()
                    .orElse(null);

            if (retornada == null) {
                ctx.status(404).result("Tarefa não encontrada.");
            } else {
                ctx.json(retornada);
            }
        });
    }

    // Lista para as tarefas serem armazenadas
    public static List<Tarefa> listaTarefas = new ArrayList<>();

    //  Classe Mensagem para representar a estrutura do JSON recebido (/echo)
    public static class Mensagem {
        public String mensagem;

        // Construtor vazio do jackson
        public Mensagem() {}

        public Mensagem(String mensagem) {
            this.mensagem = mensagem;
        }
    }
}