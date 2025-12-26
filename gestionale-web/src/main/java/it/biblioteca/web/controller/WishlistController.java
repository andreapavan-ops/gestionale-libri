package it.biblioteca.web.controller;

import it.biblioteca.web.entity.WishlistItemEntity;
import it.biblioteca.web.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public String wishlistHome(Model model) {
        model.addAttribute("utenti", wishlistService.getUtenti());
        return "wishlist-home";
    }

    @GetMapping("/{utente}")
    public String wishlistUtente(@PathVariable("utente") String utente, Model model) {
        model.addAttribute("utente", utente);
        model.addAttribute("items", wishlistService.getWishlist(utente));
        model.addAttribute("totale", wishlistService.contaItems(utente));
        model.addAttribute("daAcquistare", wishlistService.contaDaAcquistare(utente));
        return "wishlist";
    }

    @GetMapping("/{utente}/nuovo")
    public String nuovoItemForm(@PathVariable("utente") String utente, Model model) {
        model.addAttribute("utente", utente);
        return "wishlist-form";
    }

    @PostMapping("/{utente}/aggiungi")
    public String aggiungiItem(@PathVariable("utente") String utente,
                               @RequestParam("titolo") String titolo,
                               @RequestParam("autore") String autore,
                               @RequestParam("note") String note,
                               RedirectAttributes redirectAttributes) {
        wishlistService.aggiungiItem(utente, titolo, autore, note);
        redirectAttributes.addFlashAttribute("successo", "Libro aggiunto alla wishlist!");
        return "redirect:/wishlist/" + utente;
    }

    @GetMapping("/{utente}/rimuovi/{id}")
    public String rimuoviItem(@PathVariable("utente") String utente,
                              @PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        wishlistService.rimuoviItem(id);
        redirectAttributes.addFlashAttribute("successo", "Libro rimosso dalla wishlist!");
        return "redirect:/wishlist/" + utente;
    }

    @GetMapping("/{utente}/acquistato/{id}")
    public String segnaAcquistato(@PathVariable("utente") String utente,
                                  @PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        wishlistService.segnaAcquistato(id);
        redirectAttributes.addFlashAttribute("successo", "Libro segnato come acquistato!");
        return "redirect:/wishlist/" + utente;
    }

    @GetMapping("/{utente}/non-acquistato/{id}")
    public String segnaNonAcquistato(@PathVariable("utente") String utente,
                                     @PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        wishlistService.segnaNonAcquistato(id);
        redirectAttributes.addFlashAttribute("successo", "Libro segnato come da acquistare!");
        return "redirect:/wishlist/" + utente;
    }

    @GetMapping("/importa")
    public String importaForm() {
        return "wishlist-importa";
    }

    @PostMapping("/importa")
    public String importaWishlist(@RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errore", "Seleziona un file JSON da importare");
            return "redirect:/wishlist/importa";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<Map<String, Object>>> wishlistJson = mapper.readValue(
                file.getInputStream(),
                new TypeReference<Map<String, List<Map<String, Object>>>>() {}
            );

            int importati = 0;

            for (Map.Entry<String, List<Map<String, Object>>> entry : wishlistJson.entrySet()) {
                String utente = entry.getKey();
                List<Map<String, Object>> items = entry.getValue();

                for (Map<String, Object> itemMap : items) {
                    try {
                        String titolo = (String) itemMap.get("titolo");
                        String autore = (String) itemMap.get("autore");
                        String note = (String) itemMap.get("note");
                        String dataAggiunta = (String) itemMap.get("dataAggiunta");
                        boolean acquistato = itemMap.get("acquistato") != null && (Boolean) itemMap.get("acquistato");

                        WishlistItemEntity item = new WishlistItemEntity(utente, titolo, autore, note);
                        if (dataAggiunta != null) item.setDataAggiunta(dataAggiunta);
                        item.setAcquistato(acquistato);
                        wishlistService.salvaItem(item);
                        importati++;
                    } catch (Exception e) {
                        // Ignora item con errori
                    }
                }
            }

            redirectAttributes.addFlashAttribute("successo",
                String.format("Importazione completata: %d elementi importati", importati));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errore", "Errore nel parsing del file JSON: " + e.getMessage());
        }

        return "redirect:/wishlist";
    }
}
