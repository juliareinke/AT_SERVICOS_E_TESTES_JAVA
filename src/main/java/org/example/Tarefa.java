package org.example;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tarefa {
    // Atributos de Tarefa
    public String id;
    public String titulo;
    public String descricao;
    public boolean concluida;
    public String dataCriacao;

    // Construtor vazio do jackson
    public Tarefa() {}

    // Construtor para Tarefa recebendo o título e descrição
    // Outros atributos são definidos pelo sistema
    public Tarefa(String titulo, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descricao = descricao;
        this.concluida = false;
        this.dataCriacao = LocalDateTime.now().toString();
    }
}
