package it.biblioteca.web.repository;

import it.biblioteca.web.entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<LibroEntity, String> {

    List<LibroEntity> findByTitoloContainingIgnoreCase(String titolo);

    List<LibroEntity> findByAutoreContainingIgnoreCase(String autore);

    List<LibroEntity> findByGenereIgnoreCase(String genere);

    List<LibroEntity> findByAnnoPubblicazione(int anno);

    List<LibroEntity> findAllByOrderByTitoloAsc();
}
