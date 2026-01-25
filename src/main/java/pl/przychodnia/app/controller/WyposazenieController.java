package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String lista(Model model) {
        model.addAttribute("sprzety", wypoRepo.findAll());
        return "wyposazenie/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("wyposazenie", new Wyposazenie());
        model.addAttribute("gabinety", gabinetRepo.findAll());
        return "wyposazenie/formularz";
    }

    // DODANA METODA EDYCJI
    @GetMapping("/edytuj/{id}")
    public String edytuj(@PathVariable String id, Model model) {
        Wyposazenie w = wypoRepo.findById(id).orElseThrow();
        model.addAttribute("wyposazenie", w);
        model.addAttribute("gabinety", gabinetRepo.findAll());
        return "wyposazenie/formularz";
    }

    @PostMapping("/zapisz")
    public String zapisz(Wyposazenie w) {
        wypoRepo.save(w);
        return "redirect:/wyposazenie";
    }

    @GetMapping("/usun/{kod}")
    public String usun(@PathVariable String kod) {
        wypoRepo.deleteById(kod);
        return "redirect:/wyposazenie";
    }
}