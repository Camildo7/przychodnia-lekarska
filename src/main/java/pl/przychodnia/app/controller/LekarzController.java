package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Lekarz;
import pl.przychodnia.app.repository.LekarzRepository;
import pl.przychodnia.app.repository.SpecjalizacjaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    public String lista(@RequestParam(required = false) String szukaj,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "nazwisko") String sortField,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Lekarz> pageLekarze;

        if (szukaj != null && !szukaj.isEmpty()) {
            pageLekarze = lekarzRepo.findByNazwiskoContainingIgnoreCaseOrNumerPwzContaining(szukaj, szukaj, pageable);
        } else {
            pageLekarze = lekarzRepo.findAll(pageable);
        }

        model.addAttribute("lekarze", pageLekarze);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("szukaj", szukaj);

        return "lekarze/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("lekarz", new Lekarz());
        model.addAttribute("dostepneSpecjalizacje", specRepo.findAll());
        model.addAttribute("isEdit", false);
        return "lekarze/formularz";
    }

    @GetMapping("/edytuj/{id}")
    public String edytuj(@PathVariable String id, Model model) {
        Lekarz l = lekarzRepo.findById(id).orElseThrow();
        model.addAttribute("lekarz", l);
        model.addAttribute("dostepneSpecjalizacje", specRepo.findAll());
        model.addAttribute("isEdit", true);
        return "lekarze/formularz";
    }

    @PostMapping("/zapisz")
    public String zapisz(@Valid @ModelAttribute("lekarz") Lekarz lekarz,
                         BindingResult result,
                         @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                         Model model) {

        if (!isEdit && lekarzRepo.existsById(lekarz.getNumerPwz())) {
            result.rejectValue("numerPwz", "error.lekarz", "Lekarz o podanym numerze PWZ już istnieje.");
        }

        if (result.hasErrors()) {
            model.addAttribute("dostepneSpecjalizacje", specRepo.findAll());
            model.addAttribute("isEdit", isEdit); // Ważne: odsyłamy flagę z powrotem
            return "lekarze/formularz";
        }

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