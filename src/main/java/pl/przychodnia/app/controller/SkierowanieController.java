package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.Skierowanie;
import pl.przychodnia.app.entity.SkierowanieId;
import pl.przychodnia.app.entity.Wizyta;
import pl.przychodnia.app.repository.SkierowanieRepository;
import pl.przychodnia.app.repository.WizytaRepository;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

@Controller
@RequestMapping("/skierowania")
public class SkierowanieController {

    private final SkierowanieRepository skierowanieRepo;
    private final WizytaRepository wizytaRepo;

    public SkierowanieController(SkierowanieRepository sr, WizytaRepository wr) {
        this.skierowanieRepo = sr;
        this.wizytaRepo = wr;
    }

    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj,
                        @RequestParam(defaultValue = "dataWystawienia") String sortField, // Domyślne pole
                        @RequestParam(defaultValue = "desc") String sortDir,              // Domyślny kierunek
                        Model model) {

        Sort.Direction direction = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortField);

        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("skierowania", skierowanieRepo.szukajSkierowan(szukaj, sort));
        } else {
            model.addAttribute("skierowania", skierowanieRepo.findAll(sort));
        }

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "skierowania/lista";
    }

    // formularz tworzenia nowego skierowania
    @GetMapping("/nowe")
    public String formularz(@RequestParam(required = false) Long idWizyty,
                            @RequestParam(defaultValue = "skierowania") String source,
                            Model model) {
        if (idWizyty == null) {
            model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
            return "skierowania/wybor_wizyty";
        }

        Wizyta w = wizytaRepo.findById(idWizyty)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wizyty"));

        Skierowanie s = new Skierowanie();
        s.setWizyta(w);
        s.setPacjent(w.getPacjent());
        s.setLekarz(w.getLekarz());
        s.setDataWystawienia(LocalDate.now());

        model.addAttribute("skierowanie", s);
        model.addAttribute("isEdit", false);
        model.addAttribute("source", source);
        return "skierowania/formularz";
    }

    // formularz edycji istniejącego skierowania
    @GetMapping("/edytuj")
    public String edytuj(@RequestParam String kod,
                         @RequestParam String pesel,
                         @RequestParam(defaultValue = "skierowania") String source,
                         Model model) {

        SkierowanieId id = new SkierowanieId(kod, pesel);
        Skierowanie s = skierowanieRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono skierowania"));

        model.addAttribute("skierowanie", s);
        model.addAttribute("isEdit", true);
        model.addAttribute("source", source);

        return "skierowania/formularz";
    }

    // zapis nowego lub edytowanego skierowania
    @PostMapping("/zapisz")
    public String zapisz(@Valid @ModelAttribute("skierowanie") Skierowanie s,
                         BindingResult result,
                         @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                         @RequestParam(defaultValue = "skierowania") String source,
                         Model model,
                         RedirectAttributes ra) {

        if (!isEdit && skierowanieRepo.existsByKodDokumentuAndPacjent_Pesel(s.getKodDokumentu(), s.getPacjent().getPesel())) {
            result.rejectValue("kodDokumentu", "error.skierowanie",
                    "Ten kod dokumentu jest już zajęty dla tego pacjenta.");
        }

        if (result.hasErrors()) {
            if (s.getWizyta() != null && s.getWizyta().getNumerWizyty() != null) {
                Wizyta w = wizytaRepo.findById(s.getWizyta().getNumerWizyty()).orElse(null);
                if (w != null) {
                    s.setWizyta(w);
                    s.setPacjent(w.getPacjent());
                    s.setLekarz(w.getLekarz());
                }
            }

            model.addAttribute("isEdit", isEdit);
            model.addAttribute("source", source);
            return "skierowania/formularz";
        }

        try {
            skierowanieRepo.save(s);
            ra.addFlashAttribute("success", "Pomyślnie zapisano skierowanie.");

            if ("wizyta".equals(source) && s.getWizyta() != null && s.getWizyta().getNumerWizyty() != null) {
                return "redirect:/wizyta/" + s.getWizyta().getNumerWizyty();
            }

        } catch (Exception e) {
            if (s.getWizyta() != null && s.getWizyta().getNumerWizyty() != null) {
                Wizyta w = wizytaRepo.findById(s.getWizyta().getNumerWizyty()).orElse(null);
                if (w != null) {
                    s.setWizyta(w);
                    s.setPacjent(w.getPacjent());
                    s.setLekarz(w.getLekarz());
                }
            }
            model.addAttribute("error", "Błąd zapisu do bazy: " + e.getMessage());
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("source", source);
            return "skierowania/formularz";
        }

        return "redirect:/skierowania";
    }

    @GetMapping("/usun")
    public String usun(@RequestParam String kod,
                       @RequestParam String pesel,
                       @RequestParam(defaultValue = "skierowania") String source,
                       RedirectAttributes ra) {
        try {
            SkierowanieId id = new SkierowanieId(kod, pesel);
            Skierowanie s = skierowanieRepo.findById(id).orElse(null);
            Long idWizyty = (s != null && s.getWizyta() != null) ? s.getWizyta().getNumerWizyty() : null;

            skierowanieRepo.deleteById(id);
            ra.addFlashAttribute("success", "Usunięto skierowanie.");

            if ("wizyta".equals(source) && idWizyty != null) {
                return "redirect:/wizyta/" + idWizyty;
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie można usunąć skierowania.");
        }
        return "redirect:/skierowania";
    }
}