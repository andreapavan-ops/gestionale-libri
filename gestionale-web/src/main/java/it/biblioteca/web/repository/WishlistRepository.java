package it.biblioteca.web.repository;

import it.biblioteca.web.entity.WishlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItemEntity, Long> {

    List<WishlistItemEntity> findByUtenteOrderByDataAggiuntaDesc(String utente);

    List<WishlistItemEntity> findByUtenteAndAcquistatoOrderByDataAggiuntaDesc(String utente, boolean acquistato);

    long countByUtente(String utente);

    long countByUtenteAndAcquistato(String utente, boolean acquistato);
}
