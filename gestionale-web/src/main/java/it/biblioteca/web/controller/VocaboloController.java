package it.biblioteca.web.controller;

import it.biblioteca.web.entity.VocaboloEntity;
import it.biblioteca.web.service.VocaboloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class VocaboloController {

    @Autowired
    private VocaboloService vocaboloService;

    // ---- Lista parole ----

    @GetMapping("/vocabolario")
    public String listaParole(Model model) {
        List<VocaboloEntity> parole = vocaboloService.getTutteLeParole();
        model.addAttribute("parole", parole);
        model.addAttribute("totale", vocaboloService.contaParole());
        model.addAttribute("memorizzate", vocaboloService.contaMemorizzate());
        return "vocabolario";
    }

    // ---- Aggiungi parola ----

    @GetMapping("/vocabolario/aggiungi")
    public String nuovaParolaForm(Model model) {
        model.addAttribute("vocabolo", new VocaboloEntity());
        return "vocabolario-form";
    }

    @PostMapping("/vocabolario/salva")
    public String salvaParola(@ModelAttribute VocaboloEntity vocabolo,
                               RedirectAttributes redirectAttributes) {
        if (vocabolo.getParolaInglese() == null || vocabolo.getParolaInglese().isBlank()) {
            redirectAttributes.addFlashAttribute("errore", "Inserisci la parola inglese.");
            return "redirect:/vocabolario/aggiungi";
        }
        if (vocabolo.getTraduzione() == null || vocabolo.getTraduzione().isBlank()) {
            // Prova traduzione automatica
            String trad = vocaboloService.traduci(vocabolo.getParolaInglese());
            if (trad != null) {
                vocabolo.setTraduzione(trad);
            } else {
                redirectAttributes.addFlashAttribute("errore",
                        "Inserisci la traduzione manualmente (API non raggiungibile).");
                return "redirect:/vocabolario/aggiungi";
            }
        }
        vocabolo.setParolaInglese(vocabolo.getParolaInglese().trim().toLowerCase());
        vocaboloService.salvaParola(vocabolo);
        redirectAttributes.addFlashAttribute("successo",
                "Parola \"" + vocabolo.getParolaInglese() + "\" aggiunta!");
        return "redirect:/vocabolario";
    }

    // ---- Elimina parola ----

    @GetMapping("/vocabolario/elimina/{id}")
    public String eliminaParola(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vocaboloService.eliminaParola(id);
        redirectAttributes.addFlashAttribute("successo", "Parola eliminata.");
        return "redirect:/vocabolario";
    }

    // ---- API AJAX: traduzione automatica ----

    @GetMapping("/vocabolario/api/traduci")
    @ResponseBody
    public ResponseEntity<Map<String, String>> traduciParola(@RequestParam("parola") String parola) {
        String traduzione = vocaboloService.traduci(parola);
        if (traduzione != null) {
            return ResponseEntity.ok(Map.of("traduzione", traduzione));
        }
        return ResponseEntity.ok(Map.of("traduzione", ""));
    }

    // ---- Quiz ----

    @GetMapping("/vocabolario/quiz")
    public String quizPage(Model model) {
        List<VocaboloEntity> tutteLeParole = vocaboloService.getTutteLeParole();
        List<VocaboloEntity> daStudiare = vocaboloService.getParoleDaStudiare();

        if (daStudiare.isEmpty()) {
            model.addAttribute("nessunDaStudiare", true);
            model.addAttribute("parole", tutteLeParole);
            return "quiz";
        }

        // Parola corrente: prima della lista
        VocaboloEntity parolaCorrente = daStudiare.get(0);

        // Genera 4 opzioni di risposta (1 corretta + 3 sbagliate)
        List<String> opzioni = new ArrayList<>();
        opzioni.add(parolaCorrente.getTraduzione());

        // Aggiungi 3 traduzioni sbagliate prese dalle altre parole
        List<VocaboloEntity> altre = new ArrayList<>(tutteLeParole);
        altre.remove(parolaCorrente);
        Collections.shuffle(altre);
        for (int i = 0; i < Math.min(3, altre.size()); i++) {
            opzioni.add(altre.get(i).getTraduzione());
        }
        // Se non ci sono abbastanza parole, aggiungi placeholder
        while (opzioni.size() < 4) {
            opzioni.add("???");
        }
        Collections.shuffle(opzioni);

        model.addAttribute("parola", parolaCorrente);
        model.addAttribute("opzioni", opzioni);
        model.addAttribute("totaleRimanenti", daStudiare.size());
        model.addAttribute("nessunDaStudiare", false);
        return "quiz";
    }

    @PostMapping("/vocabolario/quiz/verifica")
    public String verificaRisposta(@RequestParam("idParola") Long idParola,
                                    @RequestParam("risposta") String risposta,
                                    RedirectAttributes redirectAttributes) {
        vocaboloService.getParolaById(idParola).ifPresent(parola -> {
            boolean corretto = parola.getTraduzione().trim().equalsIgnoreCase(risposta.trim());
            vocaboloService.aggiornaLivello(idParola, corretto);
            if (corretto) {
                redirectAttributes.addFlashAttribute("esito", "corretto");
                redirectAttributes.addFlashAttribute("messaggioEsito",
                        "Bravo! \"" + parola.getParolaInglese() + "\" = " + parola.getTraduzione());
            } else {
                redirectAttributes.addFlashAttribute("esito", "sbagliato");
                redirectAttributes.addFlashAttribute("messaggioEsito",
                        "Non ancora... \"" + parola.getParolaInglese() + "\" = " + parola.getTraduzione());
            }
        });
        return "redirect:/vocabolario/quiz";
    }
}
