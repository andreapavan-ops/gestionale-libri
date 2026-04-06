package it.biblioteca.web.service;

import it.biblioteca.web.entity.VocaboloEntity;
import it.biblioteca.web.repository.VocaboloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VocaboloService {

    @Autowired
    private VocaboloRepository vocaboloRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String MYMEMORY_API = "https://api.mymemory.translated.net/get?q={parola}&langpair=en|it";

    public List<VocaboloEntity> getTutteLeParole() {
        return vocaboloRepository.findAll();
    }

    public Optional<VocaboloEntity> getParolaById(Long id) {
        return vocaboloRepository.findById(id);
    }

    public VocaboloEntity salvaParola(VocaboloEntity vocabolo) {
        return vocaboloRepository.save(vocabolo);
    }

    public void eliminaParola(Long id) {
        vocaboloRepository.deleteById(id);
    }

    public long contaParole() {
        return vocaboloRepository.count();
    }

    public long contaMemorizzate() {
        return vocaboloRepository.findAll().stream()
                .filter(v -> v.getLivello() == 3)
                .count();
    }

    /**
     * Chiama MyMemory API gratuita per tradurre una parola inglese in italiano.
     * Restituisce null in caso di errore.
     */
    @SuppressWarnings("unchecked")
    public String traduci(String parolaInglese) {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    MYMEMORY_API, Map.class, parolaInglese.trim());
            if (response != null && "200".equals(String.valueOf(response.get("responseStatus")))) {
                Map<String, Object> responseData = (Map<String, Object>) response.get("responseData");
                if (responseData != null) {
                    return (String) responseData.get("translatedText");
                }
            }
        } catch (Exception e) {
            // API non raggiungibile, restituisce null
        }
        return null;
    }

    /**
     * Restituisce parole da studiare (livello < 3), mescolate casualmente.
     */
    public List<VocaboloEntity> getParoleDaStudiare() {
        List<VocaboloEntity> parole = vocaboloRepository.findByLivelloLessThan(3);
        Collections.shuffle(parole);
        return parole;
    }

    /**
     * Aggiorna il livello di apprendimento dopo un esercizio.
     * Risposta corretta: livello +1 (max 3). Risposta errata: livello -1 (min 0).
     */
    public void aggiornaLivello(Long id, boolean corretto) {
        vocaboloRepository.findById(id).ifPresent(vocabolo -> {
            int nuovoLivello = vocabolo.getLivello() + (corretto ? 1 : -1);
            vocabolo.setLivello(Math.max(0, Math.min(3, nuovoLivello)));
            vocaboloRepository.save(vocabolo);
        });
    }
}
