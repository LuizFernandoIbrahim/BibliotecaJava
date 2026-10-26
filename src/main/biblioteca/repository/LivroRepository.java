package biblioteca.repository;

import biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório de acesso aos dados de Livro.
 * O Spring Data JPA gera automaticamente os métodos de CRUD
 * (save, findAll, findById, deleteById, existsById...).
 */
@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
}
