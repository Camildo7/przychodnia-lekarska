package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    // Formularz dodawania (NOWY)
    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("lek", new Lek());
        model.addAttribute("isEdit", false); // TO WAŻNE: Pole EAN będzie aktywne
        return "leki/formularz";
    }

    // Formularz edycji (EDYTUJ)
    @GetMapping("/edytuj/{ean}")
    public String edytuj(@PathVariable String ean, Model model) {
        Lek lek = lekRepo.findById(ean).orElseThrow(() -> new IllegalArgumentException("Brak leku o podanym EAN"));
        model.addAttribute("lek", lek);
        model.addAttribute("isEdit", true); // TO WAŻNE: Pole EAN będzie zablokowane (readonly)
        return "leki/formularz";
    }

    @PostMapping("/zapisz")
    public String zapisz(@Valid @ModelAttribute("lek") Lek lek,
                         BindingResult result,
                         @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                         Model model) {

        // 1. Ochrona przed duplikatem EAN (tylko przy tworzeniu nowego)
        if (!isEdit && lekRepo.existsById(lek.getKodEan())) {
            result.rejectValue("kodEan", "error.lek", "Lek o podanym kodzie EAN już istnieje w bazie.");
        }

        // 2. Obsługa błędów walidacji (np. EAN ma 12 cyfr zamiast 13)
        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit); // Odsyłamy flagę, żeby formularz wiedział jak wyświetlić pole
            return "leki/formularz";
        }

        lekRepo.save(lek);
        return "redirect:/leki";
    }

    // Usuwanie
    @GetMapping("/usun/{ean}")
    public String usun(@PathVariable String ean, Model model) {
        try {
            lekRepo.deleteById(ean);
        } catch (Exception e) {
            // Obsługa błędu klucza obcego (jeśli lek jest na recepcie)
            model.addAttribute("error", "Nie można usunąć leku, ponieważ został już przypisany do recepty.");
            model.addAttribute("leki", lekRepo.findAll());
            return "leki/lista";
        }
        return "redirect:/leki";
    }
}