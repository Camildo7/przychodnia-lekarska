package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Lek;
import pl.przychodnia.app.repository.LekRepository;

@Controller
@RequestMapping("/leki")
public class LekController {

    private final LekRepository lekRepo;

    public LekController(LekRepository lekRepo) {
        this.lekRepo = lekRepo;
    }

    // Lista leków + wyszukiwanie
    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj, Model model) {
        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("leki", lekRepo.findByNazwaHandlowaContainingIgnoreCase(szukaj));
        } else {
            model.addAttribute("leki", lekRepo.findAll());
        }
        return "leki/lista";
    }

    // Formularz dodawania
    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("lek", new Lek());
        return "leki/formularz";
    }

    // Edycja leku (pobranie danych do formularza)
    @GetMapping("/edytuj/{ean}")
    public String edytuj(@PathVariable String ean, Model model) {
        Lek lek = lekRepo.findById(ean).orElseThrow(() -> new IllegalArgumentException("Brak leku o EAN: " + ean));
        model.addAttribute("lek", lek);
        return "leki/formularz";
    }

    // Zapis (Insert lub Update)
    @PostMapping("/zapisz")
    public String zapisz(Lek lek) {
        lekRepo.save(lek);
        return "redirect:/leki";
    }

    // Usuwanie
    @GetMapping("/usun/{ean}")
    public String usun(@PathVariable String ean, Model model) {
        try {
            lekRepo.deleteById(ean);
        } catch (Exception e) {
            // Obsługa błędu FK (jeśli lek jest na recepcie, nie można go usunąć)
            model.addAttribute("error", "Nie można usunąć leku, ponieważ jest przypisany do recept.");
            model.addAttribute("leki", lekRepo.findAll());
            return "leki/lista";
        }
        return "redirect:/leki";
    }
}