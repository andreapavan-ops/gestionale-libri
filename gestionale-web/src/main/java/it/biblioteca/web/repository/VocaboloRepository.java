package it.biblioteca.web.repository;

import it.biblioteca.web.entity.VocaboloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocaboloRepository extends JpaRepository<VocaboloEntity, Long> {
    List<VocaboloEntity> findByLivelloLessThan(int livello);
    List<VocaboloEntity> findByParolaIngleseContainingIgnoreCase(String parola);
}
