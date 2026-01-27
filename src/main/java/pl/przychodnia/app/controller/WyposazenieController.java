package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Wyposazenie;
import pl.przychodnia.app.repository.GabinetRepository;
import pl.przychodnia.app.repository.WyposazenieRepository;

@Controller
@RequestMapping("/wyposazenie")
public class WyposazenieController {

    private final WyposazenieRepository wypoRepo;
    private final GabinetRepository gabinetRepo;

    public WyposazenieController(WyposazenieRepository wr, GabinetRepository gr) {
        this.wypoRepo = wr;
        this.gabinetRepo = gr;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj, Model model) {
        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("sprzety", wypoRepo.szukajSprzetu(szukaj));
        } else {
            model.addAttribute("sprzety", wypoRepo.findAll());
        }
        return "wyposazenie/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("wyposazenie", new Wyposazenie());
        model.addAttribute("gabinety", gabinetRepo.findAll());
        model.addAttribute("isEdit", false);
        return "wyposazenie/formularz";
    }

    // DODANA METODA EDYCJI
    @GetMapping("/edytuj/{id}")
    public String edytuj(@PathVariable String id, Model model) {
        Wyposazenie w = wypoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Błędny kod sprzętu: " + id));
        model.addAttribute("wyposazenie", w);
        model.addAttribute("gabinety", gabinetRepo.findAll());
        model.addAttribute("isEdit", true);
        return "wyposazenie/formularz";
    }

    @PostMapping("/zapisz")
    public String zapisz(@Valid @ModelAttribute("wyposazenie") Wyposazenie w,
                         BindingResult result,
                         @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                         Model model) {

        // 4. Walidacja DUPLIKATU ID:
        if (!isEdit && wypoRepo.existsById(w.getKodInwentarzowy())) {
            result.rejectValue("kodInwentarzowy", "error.wyposazenie", "Sprzęt o takim kodzie już istnieje.");
        }

        // 2. Jeśli są błędy walidacji -> wracamy do formularza
        if (result.hasErrors()) {
            model.addAttribute("gabinety", gabinetRepo.findAll());
            model.addAttribute("isEdit", isEdit);
            return "wyposazenie/formularz";
        }

        try {
            wypoRepo.save(w);
            return "redirect:/wyposazenie";
        } catch (Exception e) {
            model.addAttribute("error", "Błąd zapisu bazy danych: " + e.getMessage());
            model.addAttribute("gabinety", gabinetRepo.findAll());
            model.addAttribute("isEdit", isEdit);
            return "wyposazenie/formularz";
        }
    }

    @GetMapping("/usun/{kod}")
    public String usun(@PathVariable String kod) {
        try {
            wypoRepo.deleteById(kod);
        } catch (Exception e) {
            return "redirect:/wyposazenie?error=Nie mozna usunac sprzetu";
        }
        return "redirect:/wyposazenie";
    }
}