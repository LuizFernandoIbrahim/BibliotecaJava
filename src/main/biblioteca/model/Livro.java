package biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entidade que representa um livro da biblioteca pessoal.
 */
@Entity
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    @Column(nullable = false)
    private String autor;

    @NotBlank(message = "O gênero é obrigatório")
    @Column(nullable = false)
    private String genero;

    @NotNull(message = "O ano de publicação é obrigatório")
    @Min(value = 1000, message = "Ano de publicação inválido")
    @Max(value = 2100, message = "Ano de publicação inválido")
    private Integer anoPublicacao;

    @NotNull(message = "O status de leitura é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLeitura statusLeitura;

    public Livro() {
    }

    public Livro(String titulo, String autor, String genero, Integer anoPublicacao, StatusLeitura statusLeitura) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
        this.statusLeitura = statusLeitura;
    }

    /**
     * Copia os dados de outro livro para este (usado na atualização).
     */
    public void atualizarDados(Livro outro) {
        this.titulo = outro.getTitulo();
        this.autor = outro.getAutor();
        this.genero = outro.getGenero();
        this.anoPublicacao = outro.getAnoPublicacao();
        this.statusLeitura = outro.getStatusLeitura();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public StatusLeitura getStatusLeitura() { return statusLeitura; }
    public void setStatusLeitura(StatusLeitura statusLeitura) { this.statusLeitura = statusLeitura; }

    @Override
    public String toString() {
        return "Livro{id=" + id + ", titulo='" + titulo + "', autor='" + autor + "', genero='" + genero
                + "', anoPublicacao=" + anoPublicacao + ", statusLeitura=" + statusLeitura + "}";
    }
}
