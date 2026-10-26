package biblioteca.test.controller;

import biblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LivroControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LIVRO_JSON = """
            {
              "titulo": "O Hobbit",
              "autor": "J.R.R. Tolkien",
              "genero": "Fantasia",
              "anoPublicacao": 1937,
              "statusLeitura": "NAO_LIDO"
            }
            """;

    @BeforeEach
    void limparBanco() {
        repository.deleteAll();
    }

    private long cadastrarLivro() throws Exception {
        String resposta = mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIVRO_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(resposta);
        return json.get("id").asLong();
    }

    @Test
    @DisplayName("POST /api/livros - cadastra um livro e retorna 201")
    void cadastrar() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LIVRO_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("O Hobbit"))
                .andExpect(jsonPath("$.statusLeitura").value("NAO_LIDO"));
    }

    @Test
    @DisplayName("POST /api/livros - rejeita dados inválidos com 400")
    void cadastrarInvalido() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"\", \"autor\": \"Alguém\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").exists())
                .andExpect(jsonPath("$.campos.genero").exists());
    }

    @Test
    @DisplayName("GET /api/livros - lista todos os livros")
    void listar() throws Exception {
        cadastrarLivro();
        cadastrarLivro();

        mockMvc.perform(get("/api/livros"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/livros/{id} - consulta um livro pelo id")
    void consultar() throws Exception {
        long id = cadastrarLivro();

        mockMvc.perform(get("/api/livros/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.autor").value("J.R.R. Tolkien"));
    }

    @Test
    @DisplayName("GET /api/livros/{id} - retorna 404 para id inexistente")
    void consultarInexistente() throws Exception {
        mockMvc.perform(get("/api/livros/{id}", 9999))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Livro com id 9999 não encontrado"));
    }

    @Test
    @DisplayName("PUT /api/livros/{id} - atualiza um livro")
    void atualizar() throws Exception {
        long id = cadastrarLivro();

        String atualizado = """
                {
                  "titulo": "O Hobbit",
                  "autor": "J.R.R. Tolkien",
                  "genero": "Fantasia",
                  "anoPublicacao": 1937,
                  "statusLeitura": "LIDO"
                }
                """;

        mockMvc.perform(put("/api/livros/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizado))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.statusLeitura").value("LIDO"));
    }

    @Test
    @DisplayName("DELETE /api/livros/{id} - exclui um livro e depois retorna 404")
    void excluir() throws Exception {
        long id = cadastrarLivro();

        mockMvc.perform(delete("/api/livros/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/livros/{id}", id))
                .andExpect(status().isNotFound());
    }
}
