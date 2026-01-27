package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Gabinet;
import pl.przychodnia.app.entity.Specjalizacja;
import pl.przychodnia.app.repository.GabinetRepository;
import pl.przychodnia.app.repository.SpecjalizacjaRepository;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin")
public class ZarzadzanieController {

    private final GabinetRepository gabinetRepo;
    private final SpecjalizacjaRepository specRepo;

    public ZarzadzanieController(GabinetRepository gr, SpecjalizacjaRepository sr) {
        this.gabinetRepo = gr;
        this.specRepo = sr;
    }

    // --- GABINETY ---
    @GetMapping("/gabinety")
    public String listaGabinetow(@RequestParam(required = false) String szukaj,
                                 @RequestParam(defaultValue = "numerGabinetu") String sortField,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);

        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("gabinety", gabinetRepo.findByOpisFunkcjiContainingIgnoreCase(szukaj, sort));
        } else {
            model.addAttribute("gabinety", gabinetRepo.findAll(sort));
        }

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "gabinety/lista";
    }

    @GetMapping("/gabinety/nowy")
    public String nowyGabinet(Model model) {
        model.addAttribute("gabinet", new Gabinet());
        return "gabinety/formularz";
    }

    @GetMapping("/gabinety/edytuj/{id}")
    public String edytujGabinet(@PathVariable Long id, Model model) {
        Gabinet g = gabinetRepo.findById(id).orElseThrow();
        model.addAttribute("gabinet", g);
        return "gabinety/formularz";
    }

    @PostMapping("/gabinety/zapisz")
    public String zapiszGabinet(@Valid @ModelAttribute("gabinet") Gabinet g,
                                BindingResult result,
                                Model model) {

        // Gabinety mają ID generowane automatycznie, więc nie ma ryzyka nadpisania przy tworzeniu.
        // Standardowa walidacja wystarczy.

        if (result.hasErrors()) {
            return "gabinety/formularz";
        }

        gabinetRepo.save(g);
        return "redirect:/admin/gabinety";
    }

    @GetMapping("/gabinety/usun/{id}")
    public String usunGabinet(@PathVariable Long id) {
        try {
            gabinetRepo.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/gabinety?error=true";
        }
        return "redirect:/admin/gabinety";
    }

    // --- SPECJALIZACJE ---

    @GetMapping("/specjalizacje")
    public String listaSpecjalizacji(@RequestParam(required = false) String szukaj,
                                     @RequestParam(defaultValue = "nazwaSpecjalizacji") String sortField,
                                     @RequestParam(defaultValue = "asc") String sortDir,
                                     Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);

        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("specjalizacje", specRepo.findByNazwaSpecjalizacjiContainingIgnoreCase(szukaj, sort));
        } else {
            model.addAttribute("specjalizacje", specRepo.findAll(sort));
        }

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "specjalizacje/lista";
    }

    @GetMapping("/specjalizacje/nowa")
    public String nowaSpecjalizacja(Model model) {
        model.addAttribute("specjalizacja", new Specjalizacja());
        model.addAttribute("isEdit", false);
        return "specjalizacje/formularz";
    }

    @GetMapping("/specjalizacje/edytuj/{id}")
    public String edytujSpecjalizacje(@PathVariable String id, Model model) {
        Specjalizacja s = specRepo.findById(id).orElseThrow();
        model.addAttribute("specjalizacja", s);
        model.addAttribute("isEdit", true);
        return "specjalizacje/formularz";
    }

    @PostMapping("/specjalizacje/zapisz")
    public String zapiszSpecjalizacje(@Valid @ModelAttribute("specjalizacja") Specjalizacja s,
                                      BindingResult result,
                                      // 3. Pobieramy flagę isEdit
                                      @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                                      Model model) {

        // 4. Zabezpieczenie przed duplikatem ID
        if (!isEdit && specRepo.existsById(s.getNazwaSpecjalizacji())) {
            result.rejectValue("nazwaSpecjalizacji", "error.specjalizacja", "Taka specjalizacja już istnieje.");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "specjalizacje/formularz";
        }

        specRepo.save(s);
        return "redirect:/admin/specjalizacje";
    }

    @GetMapping("/specjalizacje/usun/{id}")
    public String usunSpecjalizacje(@PathVariable String id) {
        try {
            specRepo.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/specjalizacje?error=true";
        }
        return "redirect:/admin/specjalizacje";
    }




}