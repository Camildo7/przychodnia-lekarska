package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Wyposazenie;
import pl.przychodnia.app.repository.GabinetRepository;
import pl.przychodnia.app.repository.WyposazenieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    public String lista(@RequestParam(required = false) String szukaj,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "nazwaSprzetu") String sortField,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Wyposazenie> pageSprzety;

        if (szukaj != null && !szukaj.isEmpty()) {
            pageSprzety = wypoRepo.szukajSprzetu(szukaj, pageable);
        } else {
            pageSprzety = wypoRepo.findAll(pageable);
        }

        model.addAttribute("sprzety", pageSprzety);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("szukaj", szukaj);

        return "wyposazenie/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("wyposazenie", new Wyposazenie());
        model.addAttribute("gabinety", gabinetRepo.findAll());
        model.addAttribute("isEdit", false);
        return "wyposazenie/formularz";
    }

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

        if (!isEdit && wypoRepo.existsById(w.getKodInwentarzowy())) {
            result.rejectValue("kodInwentarzowy", "error.wyposazenie", "Sprzęt o takim kodzie już istnieje.");
        }

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