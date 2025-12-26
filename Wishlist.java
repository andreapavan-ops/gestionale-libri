import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Wishlist {
    private Map<String, List<LibroWishlist>> wishlistUtenti;
    private static final String FILE_WISHLIST = "wishlist.json";
    private Gson gson;

    public Wishlist() {
        this.wishlistUtenti = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        caricaWishlist();

        // Inizializza gli utenti se non esistono
        if (!wishlistUtenti.containsKey("Carlo")) {
            wishlistUtenti.put("Carlo", new ArrayList<>());
        }
        if (!wishlistUtenti.containsKey("AnnaMaria")) {
            wishlistUtenti.put("AnnaMaria", new ArrayList<>());
        }
    }

    // Aggiunge un libro alla wishlist di un utente
    public void aggiungiLibro(String utente, LibroWishlist libro) {
        if (!wishlistUtenti.containsKey(utente)) {
            wishlistUtenti.put(utente, new ArrayList<>());
        }
        wishlistUtenti.get(utente).add(libro);
        salvaWishlist();
    }

    // Rimuove un libro dalla wishlist
    public boolean rimuoviLibro(String utente, int indice) {
        if (wishlistUtenti.containsKey(utente)) {
            List<LibroWishlist> lista = wishlistUtenti.get(utente);
            if (indice >= 0 && indice < lista.size()) {
                lista.remove(indice);
                salvaWishlist();
                return true;
            }
        }
        return false;
    }

    // Segna un libro come acquistato
    public void segnaAcquistato(String utente, int indice) {
        if (wishlistUtenti.containsKey(utente)) {
            List<LibroWishlist> lista = wishlistUtenti.get(utente);
            if (indice >= 0 && indice < lista.size()) {
                lista.get(indice).setAcquistato(true);
                salvaWishlist();
            }
        }
    }

    // Ottiene la wishlist di un utente
    public List<LibroWishlist> getWishlist(String utente) {
        return wishlistUtenti.getOrDefault(utente, new ArrayList<>());
    }

    // Ottiene tutti gli utenti
    public Set<String> getUtenti() {
        return wishlistUtenti.keySet();
    }

    // Conta i libri nella wishlist di un utente
    public int getNumeroLibri(String utente) {
        return getWishlist(utente).size();
    }

    // Salva su file JSON
    private void salvaWishlist() {
        try (Writer writer = new FileWriter(FILE_WISHLIST)) {
            gson.toJson(wishlistUtenti, writer);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio wishlist: " + e.getMessage());
        }
    }

    // Carica da file JSON
    private void caricaWishlist() {
        File file = new File(FILE_WISHLIST);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type type = new TypeToken<Map<String, List<LibroWishlist>>>(){}.getType();
                Map<String, List<LibroWishlist>> caricato = gson.fromJson(reader, type);
                if (caricato != null) {
                    wishlistUtenti = caricato;
                }
            } catch (IOException e) {
                System.err.println("Errore nel caricamento wishlist: " + e.getMessage());
            }
        }
    }
}
