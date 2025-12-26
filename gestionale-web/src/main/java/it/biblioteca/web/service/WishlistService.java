package it.biblioteca.web.service;

import it.biblioteca.web.entity.WishlistItemEntity;
import it.biblioteca.web.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    private static final List<String> UTENTI = Arrays.asList("Carlo", "AnnaMaria");

    public List<String> getUtenti() {
        return UTENTI;
    }

    public List<WishlistItemEntity> getWishlist(String utente) {
        return wishlistRepository.findByUtenteOrderByDataAggiuntaDesc(utente);
    }

    public List<WishlistItemEntity> getWishlistDaAcquistare(String utente) {
        return wishlistRepository.findByUtenteAndAcquistatoOrderByDataAggiuntaDesc(utente, false);
    }

    public List<WishlistItemEntity> getWishlistAcquistati(String utente) {
        return wishlistRepository.findByUtenteAndAcquistatoOrderByDataAggiuntaDesc(utente, true);
    }

    public WishlistItemEntity aggiungiItem(String utente, String titolo, String autore, String note) {
        WishlistItemEntity item = new WishlistItemEntity(utente, titolo, autore, note);
        return wishlistRepository.save(item);
    }

    public void rimuoviItem(Long id) {
        wishlistRepository.deleteById(id);
    }

    public void segnaAcquistato(Long id) {
        Optional<WishlistItemEntity> itemOpt = wishlistRepository.findById(id);
        if (itemOpt.isPresent()) {
            WishlistItemEntity item = itemOpt.get();
            item.setAcquistato(true);
            wishlistRepository.save(item);
        }
    }

    public void segnaNonAcquistato(Long id) {
        Optional<WishlistItemEntity> itemOpt = wishlistRepository.findById(id);
        if (itemOpt.isPresent()) {
            WishlistItemEntity item = itemOpt.get();
            item.setAcquistato(false);
            wishlistRepository.save(item);
        }
    }

    public long contaItems(String utente) {
        return wishlistRepository.countByUtente(utente);
    }

    public long contaDaAcquistare(String utente) {
        return wishlistRepository.countByUtenteAndAcquistato(utente, false);
    }

    public WishlistItemEntity salvaItem(WishlistItemEntity item) {
        return wishlistRepository.save(item);
    }
}
