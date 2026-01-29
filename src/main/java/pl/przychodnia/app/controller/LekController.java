package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Lek;
import pl.przychodnia.app.repository.LekRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/leki")
public class LekController {

    private final LekRepository lekRepo;

    public LekController(LekRepository lekRepo) {
        this.lekRepo = lekRepo;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "nazwaHandlowa") String sortField,
                        @RequestParam(defaultValue = "asc") String sortDir,
                        Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Lek> pageLekow;

        if (szukaj != null && !szukaj.isEmpty()) {
            pageLekow = lekRepo.szukajLekow(szukaj, pageable);
        } else {
            pageLekow = lekRepo.findAll(pageable);
        }

        model.addAttribute("leki", pageLekow);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("szukaj", szukaj);

        return "leki/lista";
    }

    @GetMapping("/nowy")
    public String formularz(Model model) {
        model.addAttribute("lek", new Lek());
        model.addAttribute("isEdit", false); // TO WAŻNE: Pole EAN będzie aktywne
        return "leki/formularz";
    }

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

        if (!isEdit && lekRepo.existsById(lek.getKodEan())) {
            result.rejectValue("kodEan", "error.lek", "Lek o podanym kodzie EAN już istnieje w bazie.");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "leki/formularz";
        }

        lekRepo.save(lek);
        return "redirect:/leki";
    }

    @GetMapping("/usun/{ean}")
    public String usun(@PathVariable String ean, Model model) {
        try {
            lekRepo.deleteById(ean);
        } catch (Exception e) {
            model.addAttribute("error", "Nie można usunąć leku, ponieważ został już przypisany do recepty.");
            model.addAttribute("leki", lekRepo.findAll());
            return "leki/lista";
        }
        return "redirect:/leki";
    }
}