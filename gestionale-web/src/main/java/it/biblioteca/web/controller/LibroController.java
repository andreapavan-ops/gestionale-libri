package it.biblioteca.web.controller;

import it.biblioteca.web.entity.LibroEntity;
import it.biblioteca.web.entity.RecensioneEntity;
import it.biblioteca.web.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("libri", libroService.getTuttiLibri());
        model.addAttribute("totaleLibri", libroService.contaLibri());
        return "index";
    }

    @GetMapping("/libri")
    public String listaLibri(Model model) {
        model.addAttribute("libri", libroService.getTuttiLibri());
        model.addAttribute("totaleLibri", libroService.contaLibri());
        return "libri";
    }

    @GetMapping("/libri/nuovo")
    public String nuovoLibroForm(Model model) {
        model.addAttribute("libro", new LibroEntity());
        return "libro-form";
    }

    @PostMapping("/libri/salva")
    public String salvaLibro(@ModelAttribute LibroEntity libro, RedirectAttributes redirectAttributes) {
        // Validazione ISBN
        String erroreIsbn = LibroEntity.getIsbnValidationMessage(libro.getIsbn());
        if (erroreIsbn != null) {
            redirectAttributes.addFlashAttribute("errore", erroreIsbn);
            return "redirect:/libri/nuovo";
        }

        // Controlla se ISBN esiste già (solo per nuovi libri)
        if (libroService.esisteIsbn(libro.getIsbn())) {
            // Aggiorna libro esistente
            libroService.salvaLibro(libro);
            redirectAttributes.addFlashAttribute("successo", "Libro aggiornato con successo!");
        } else {
            // Nuovo libro
            libroService.salvaLibro(libro);
            redirectAttributes.addFlashAttribute("successo", "Libro aggiunto con successo!");
        }

        return "redirect:/libri";
    }

    @GetMapping("/libri/modifica/{isbn}")
    public String modificaLibroForm(@PathVariable("isbn") String isbn, Model model) {
        return libroService.getLibroByIsbn(isbn)
            .map(libro -> {
                model.addAttribute("libro", libro);
                return "libro-form";
            })
            .orElse("redirect:/libri");
    }

    @GetMapping("/libri/elimina/{isbn}")
    public String eliminaLibro(@PathVariable("isbn") String isbn, RedirectAttributes redirectAttributes) {
        libroService.eliminaLibro(isbn);
        redirectAttributes.addFlashAttribute("successo", "Libro eliminato con successo!");
        return "redirect:/libri";
    }

    @GetMapping("/libri/dettaglio/{isbn}")
    public String dettaglioLibro(@PathVariable("isbn") String isbn, Model model) {
        return libroService.getLibroByIsbn(isbn)
            .map(libro -> {
                model.addAttribute("libro", libro);
                return "libro-dettaglio";
            })
            .orElse("redirect:/libri");
    }

    @GetMapping("/cerca")
    public String cerca(@RequestParam("q") String q, Model model) {
        var risultatiTitolo = libroService.cercaPerTitolo(q);
        var risultatiAutore = libroService.cercaPerAutore(q);

        // Unisci risultati senza duplicati
        risultatiAutore.stream()
            .filter(l -> !risultatiTitolo.contains(l))
            .forEach(risultatiTitolo::add);

        model.addAttribute("libri", risultatiTitolo);
        model.addAttribute("query", q);
        model.addAttribute("totaleLibri", risultatiTitolo.size());
        return "libri";
    }

    @PostMapping("/libri/{isbn}/recensione")
    public String aggiungiRecensione(@PathVariable("isbn") String isbn,
                                     @RequestParam("utente") String utente,
                                     @RequestParam("stelle") int stelle,
                                     @RequestParam("commento") String commento,
                                     RedirectAttributes redirectAttributes) {
        RecensioneEntity recensione = new RecensioneEntity(utente, stelle, commento);
        libroService.aggiungiRecensione(isbn, recensione);
        redirectAttributes.addFlashAttribute("successo", "Recensione aggiunta!");
        return "redirect:/libri/dettaglio/" + isbn;
    }

    @GetMapping("/libri/importa")
    public String importaForm() {
        return "importa";
    }

    @PostMapping("/libri/importa")
    public String importaLibri(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errore", "Seleziona un file JSON da importare");
            return "redirect:/libri/importa";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> libriJson = mapper.readValue(
                file.getInputStream(),
                new TypeReference<List<Map<String, Object>>>() {}
            );

            int importati = 0;
            int aggiornati = 0;
            int errori = 0;

            for (Map<String, Object> libroMap : libriJson) {
                try {
                    String isbn = (String) libroMap.get("isbn");
                    String titolo = (String) libroMap.get("titolo");
                    String autore = (String) libroMap.get("autore");
                    int anno = ((Number) libroMap.get("annoPubblicazione")).intValue();
                    String genere = (String) libroMap.get("genere");

                    LibroEntity libro = new LibroEntity();
                    libro.setIsbn(isbn);
                    libro.setTitolo(titolo);
                    libro.setAutore(autore);
                    libro.setAnnoPubblicazione(anno);
                    libro.setGenere(genere);

                    // Importa anche le recensioni se presenti
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> recensioniJson = (List<Map<String, Object>>) libroMap.get("recensioni");
                    if (recensioniJson != null) {
                        for (Map<String, Object> recMap : recensioniJson) {
                            String utente = (String) recMap.get("utente");
                            int stelle = ((Number) recMap.get("stelle")).intValue();
                            String commento = (String) recMap.get("commento");
                            String data = (String) recMap.get("data");
                            RecensioneEntity rec = new RecensioneEntity(utente, stelle, commento);
                            if (data != null) rec.setData(data);
                            libro.aggiungiRecensione(rec);
                        }
                    }

                    if (libroService.esisteIsbn(isbn)) {
                        aggiornati++;
                    } else {
                        importati++;
                    }
                    libroService.salvaLibro(libro);
                } catch (Exception e) {
                    errori++;
                }
            }

            String messaggio = String.format("Importazione completata: %d nuovi, %d aggiornati", importati, aggiornati);
            if (errori > 0) {
                messaggio += String.format(", %d errori", errori);
            }
            redirectAttributes.addFlashAttribute("successo", messaggio);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", "Errore nel parsing del file JSON: " + e.getMessage());
        }

        return "redirect:/libri";
    }

    @GetMapping("/libri/esporta")
    public ResponseEntity<byte[]> esportaLibri() {
        try {
            List<LibroEntity> libri = libroService.getTuttiLibri();
            List<Map<String, Object>> libriJson = new ArrayList<>();

            for (LibroEntity libro : libri) {
                Map<String, Object> libroMap = new HashMap<>();
                libroMap.put("isbn", libro.getIsbn());
                libroMap.put("titolo", libro.getTitolo());
                libroMap.put("autore", libro.getAutore());
                libroMap.put("annoPubblicazione", libro.getAnnoPubblicazione());
                libroMap.put("genere", libro.getGenere());

                // Aggiungi recensioni
                List<Map<String, Object>> recensioniJson = new ArrayList<>();
                for (RecensioneEntity rec : libro.getRecensioni()) {
                    Map<String, Object> recMap = new HashMap<>();
                    recMap.put("utente", rec.getUtente());
                    recMap.put("stelle", rec.getStelle());
                    recMap.put("commento", rec.getCommento());
                    recMap.put("data", rec.getData());
                    recensioniJson.add(recMap);
                }
                libroMap.put("recensioni", recensioniJson);

                libriJson.add(libroMap);
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            byte[] jsonBytes = mapper.writeValueAsBytes(libriJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "libri.json");

            return ResponseEntity.ok()
                .headers(headers)
                .body(jsonBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
