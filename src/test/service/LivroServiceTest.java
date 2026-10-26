package biblioteca.test.service;

import biblioteca.exception.LivroNaoEncontradoException;
import biblioteca.model.Livro;
import biblioteca.model.StatusLeitura;
import biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository repository;

    @InjectMocks
    private LivroService service;

    private Livro livro;

    @BeforeEach
    void setUp() {
        livro = new Livro("Dom Casmurro", "Machado de Assis", "Romance", 1899, StatusLeitura.LIDO);
        livro.setId(1L);
    }

    @Test
    @DisplayName("Cadastrar: deve salvar e retornar o livro")
    void cadastrar() {
        when(repository.save(any(Livro.class))).thenReturn(livro);

        Livro salvo = service.cadastrar(new Livro("Dom Casmurro", "Machado de Assis", "Romance", 1899, StatusLeitura.LIDO));

        assertThat(salvo.getId()).isEqualTo(1L);
        assertThat(salvo.getTitulo()).isEqualTo("Dom Casmurro");
        verify(repository).save(any(Livro.class));
    }

    @Test
    @DisplayName("Listar: deve retornar todos os livros")
    void listar() {
        Livro outro = new Livro("1984", "George Orwell", "Distopia", 1949, StatusLeitura.LENDO);
        when(repository.findAll()).thenReturn(List.of(livro, outro));

        List<Livro> livros = service.listar();

        assertThat(livros).hasSize(2).extracting(Livro::getTitulo).containsExactly("Dom Casmurro", "1984");
    }

    @Test
    @DisplayName("Consultar: deve retornar o livro quando o id existe")
    void consultarExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(livro));

        assertThat(service.consultar(1L).getAutor()).isEqualTo("Machado de Assis");
    }

    @Test
    @DisplayName("Consultar: deve lançar exceção quando o id não existe")
    void consultarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultar(99L))
                .isInstanceOf(LivroNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Atualizar: deve alterar os dados do livro existente")
    void atualizar() {
        when(repository.findById(1L)).thenReturn(Optional.of(livro));
        when(repository.save(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro novosDados = new Livro("Dom Casmurro (Edição Comentada)", "Machado de Assis", "Clássico", 1899, StatusLeitura.LENDO);
        Livro atualizado = service.atualizar(1L, novosDados);

        assertThat(atualizado.getId()).isEqualTo(1L);
        assertThat(atualizado.getTitulo()).isEqualTo("Dom Casmurro (Edição Comentada)");
        assertThat(atualizado.getGenero()).isEqualTo("Clássico");
        assertThat(atualizado.getStatusLeitura()).isEqualTo(StatusLeitura.LENDO);
    }

    @Test
    @DisplayName("Excluir: deve remover o livro quando o id existe")
    void excluirExistente() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Excluir: deve lançar exceção quando o id não existe")
    void excluirInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.excluir(99L)).isInstanceOf(LivroNaoEncontradoException.class);
        verify(repository, never()).deleteById(any());
    }
}
