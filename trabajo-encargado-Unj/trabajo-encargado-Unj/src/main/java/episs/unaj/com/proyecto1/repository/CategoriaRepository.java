package episs.unaj.com.proyecto1.repository;

import episs.unaj.com.proyecto1.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Optional<Categoria> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}