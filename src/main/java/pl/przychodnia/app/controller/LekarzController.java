package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Lekarz;
import pl.przychodnia.app.repository.LekarzRepository;
import pl.przychodnia.app.repository.SpecjalizacjaRepository;

import java.util.List;

@Controller
@RequestMapping("/lekarze")
public class LekarzController {

    private final LekarzRepository lekarzRepo;
    private final SpecjalizacjaRepository specRepo;

    public LekarzController(LekarzRepository lr, SpecjalizacjaRepository sr) {
        this.lekarzRepo = lr;
        this.specRepo = sr;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("lekarze", lekarzRepo.findAll());
        return "lekarze/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("lekarz", new Lekarz());
        model.addAttribute("dostepneSpecjalizacje", specRepo.findAll());
        return "lekarze/formularz";
    }

    @GetMapping("/edytuj/{id}")
    public String edytuj(@PathVariable String id, Model model) {
        Lekarz l = lekarzRepo.findById(id).orElseThrow();
        model.addAttribute("lekarz", l);
        model.addAttribute("dostepneSpecjalizacje", specRepo.findAll());
        return "lekarze/formularz";
    }

    @PostMapping("/zapisz")
    public String zapisz(Lekarz lekarz) {
        // Spring automatycznie zmapuje listę ID specjalizacji z formularza na List<Specjalizacja>
        lekarzRepo.save(lekarz);
        return "redirect:/lekarze";
    }

    @GetMapping("/usun/{id}")
    public String usun(@PathVariable String id) {
        try {
            lekarzRepo.deleteById(id);
        } catch (Exception e) {
            return "redirect:/lekarze?error=Lekarz ma przypisane wizyty";
        }
        return "redirect:/lekarze";
    }
}