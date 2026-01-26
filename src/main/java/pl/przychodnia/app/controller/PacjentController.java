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
            // Wymaga metody findByNazwiskoContainingIgnoreCase w repozytorium!
            // Jeśli jej nie masz, dodaj ją w PacjentRepository
            pacjenci = pacjentRepository.findAll().stream()
                    .filter(p -> p.getNazwisko().toLowerCase().contains(szukaj.toLowerCase()))
                    .toList();
        } else {
            pacjenci = pacjentRepository.findAll();
        }
        model.addAttribute("pacjenci", pacjenci);
        return "pacjenci/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("pacjent", new Pacjent());
        return "pacjenci/formularz";
    }

    @GetMapping("/historia/{pesel}")
    public String historia(@PathVariable String pesel, Model model) {
        var historia = pacjentService.pobierzHistorie(pesel);
        var pacjent = pacjentRepository.findById(pesel).orElseThrow();

        model.addAttribute("historia", historia);
        model.addAttribute("pacjent", pacjent);
        return "pacjenci/historia";
    }

    // Obsługa dodawania (wywołanie procedury)
    @PostMapping("/dodaj")
    public String dodaj(@Valid @ModelAttribute("pacjent") Pacjent p,
                        BindingResult result,
                        Model model) {

        if (result.hasErrors()) {
            return "pacjenci/formularz";
        }

        try {
            pacjentService.dodajPacjenta(p.getPesel(), p.getImie(), p.getNazwisko(), p.getAdresZamieszkania(), p.getTelefonKontaktowy());
            return "redirect:/pacjenci";
        } catch (Exception e) {
            String error = "Nie udało się dodać pacjenta.";
            if (e.getMessage().contains("ORA-00001")) { // Naruszenie klucza głównego (PESEL)
                error = "Pacjent o podanym numerze PESEL już istnieje w bazie!";
            }
            model.addAttribute("error", error);
            return "pacjenci/formularz";
        }
    }
}