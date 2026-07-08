package episs.unaj.com.proyecto1.repository;

import episs.unaj.com.proyecto1.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByCategoriaId(Integer categoriaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}