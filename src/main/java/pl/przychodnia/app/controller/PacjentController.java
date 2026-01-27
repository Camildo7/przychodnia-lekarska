package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Pacjent;
import pl.przychodnia.app.repository.PacjentRepository;
import pl.przychodnia.app.service.PacjentService;

import java.util.List;

@Controller
@RequestMapping("/pacjenci")
public class PacjentController {

    private final PacjentRepository pacjentRepository;
    private final PacjentService pacjentService;

    public PacjentController(PacjentRepository pacjentRepository, PacjentService pacjentService) {
        this.pacjentRepository = pacjentRepository;
        this.pacjentService = pacjentService;
    }

    // Lista z wyszukiwaniem
    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj, Model model) {
        List<Pacjent> pacjenci;
        if (szukaj != null && !szukaj.isEmpty()) {
            pacjenci = pacjentRepository.findByNazwiskoContainingIgnoreCaseOrPeselContaining(szukaj, szukaj);
        } else {
            pacjenci = pacjentRepository.findAll();
        }
        model.addAttribute("pacjenci", pacjenci);
        return "pacjenci/lista";
    }

    // Formularz dodawania
    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("pacjent", new Pacjent());
        model.addAttribute("isEdit", false);
        return "pacjenci/formularz";
    }

    // Formularz edycji
    @GetMapping("/edytuj/{pesel}")
    public String edytuj(@PathVariable String pesel, Model model) {
        Pacjent p = pacjentRepository.findById(pesel)
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy numer PESEL: " + pesel));
        model.addAttribute("pacjent", p);
        model.addAttribute("isEdit", true);
        return "pacjenci/formularz";
    }

    // Historia (bez zmian)
    @GetMapping("/historia/{pesel}")
    public String historia(@PathVariable String pesel, Model model) {
        try {
            var historia = pacjentService.pobierzHistorie(pesel);
            var pacjent = pacjentRepository.findById(pesel).orElseThrow();
            model.addAttribute("historia", historia);
            model.addAttribute("pacjent", pacjent);
            return "pacjenci/historia";
        } catch (Exception e) {
            return "redirect:/pacjenci";
        }
    }

    // Zapis (Dodawanie lub Edycja) z Walidacją
    @PostMapping("/zapisz")
    public String zapisz(@Valid @ModelAttribute("pacjent") Pacjent p,
                         BindingResult result,
                         @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                         Model model) {

        // 1. Walidacja unikalności PESEL (tylko przy dodawaniu nowego)
        if (!isEdit && pacjentRepository.existsById(p.getPesel())) {
            result.rejectValue("pesel", "error.pacjent", "Pacjent o podanym numerze PESEL już istnieje.");
        }

        // 2. Jeśli są błędy (np. cyfry w imieniu, zły telefon, duplikat PESEL), wracamy do formularza
        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "pacjenci/formularz";
        }

        try {
            if (isEdit) {
                // Edycja: JPA
                pacjentRepository.save(p);
            } else {
                // Procedura
                pacjentService.dodajPacjenta(
                        p.getPesel(),
                        p.getImie(),
                        p.getNazwisko(),
                        p.getAdresZamieszkania(),
                        p.getTelefonKontaktowy()
                );
                // Uzupełnienie emaila
                if (p.getEmail() != null && !p.getEmail().isEmpty()) {
                    pacjentRepository.save(p);
                }
            }
            return "redirect:/pacjenci";

        } catch (Exception e) {
            model.addAttribute("error", "Błąd zapisu bazy danych: " + e.getMessage());
            model.addAttribute("isEdit", isEdit);
            return "pacjenci/formularz";
        }
    }

    @GetMapping("/usun/{pesel}")
    public String usun(@PathVariable String pesel) {
        try {
            pacjentRepository.deleteById(pesel);
        } catch (Exception e) {
            return "redirect:/pacjenci?error=Pacjent posiada historie wizyt i nie moze zostac usuniety";
        }
        return "redirect:/pacjenci";
    }
}