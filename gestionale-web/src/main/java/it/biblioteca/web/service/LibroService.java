package it.biblioteca.web.service;

import it.biblioteca.web.entity.LibroEntity;
import it.biblioteca.web.entity.RecensioneEntity;
import it.biblioteca.web.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<LibroEntity> getTuttiLibri() {
        return libroRepository.findAllByOrderByTitoloAsc();
    }

    public Optional<LibroEntity> getLibroByIsbn(String isbn) {
        return libroRepository.findById(isbn);
    }

    public LibroEntity salvaLibro(LibroEntity libro) {
        return libroRepository.save(libro);
    }

    public void eliminaLibro(String isbn) {
        libroRepository.deleteById(isbn);
    }

    public boolean esisteIsbn(String isbn) {
        return libroRepository.existsById(isbn);
    }

    public List<LibroEntity> cercaPerTitolo(String titolo) {
        return libroRepository.findByTitoloContainingIgnoreCase(titolo);
    }

    public List<LibroEntity> cercaPerAutore(String autore) {
        return libroRepository.findByAutoreContainingIgnoreCase(autore);
    }

    public List<LibroEntity> cercaPerGenere(String genere) {
        return libroRepository.findByGenereIgnoreCase(genere);
    }

    public long contaLibri() {
        return libroRepository.count();
    }

    public void aggiungiRecensione(String isbn, RecensioneEntity recensione) {
        Optional<LibroEntity> libroOpt = libroRepository.findById(isbn);
        if (libroOpt.isPresent()) {
            LibroEntity libro = libroOpt.get();
            libro.aggiungiRecensione(recensione);
            libroRepository.save(libro);
        }
    }
}
