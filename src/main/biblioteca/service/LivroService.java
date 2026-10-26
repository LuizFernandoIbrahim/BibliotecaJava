package biblioteca.service;

import biblioteca.exception.LivroNaoEncontradoException;
import biblioteca.model.Livro;
import biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Livro cadastrar(Livro livro) {
        livro.setId(null);
        return repository.save(livro);
    }

    public List<Livro> listar() {
        return repository.findAll();
    }

    public Livro consultar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

    public Livro atualizar(Long id, Livro dadosNovos) {
        Livro existente = consultar(id);
        existente.atualizarDados(dadosNovos);
        return repository.save(existente);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new LivroNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
