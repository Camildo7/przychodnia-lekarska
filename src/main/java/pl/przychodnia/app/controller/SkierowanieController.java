package pl.przychodnia.app.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.Skierowanie;
import pl.przychodnia.app.entity.Wizyta;
import pl.przychodnia.app.repository.SkierowanieRepository;
import pl.przychodnia.app.repository.WizytaRepository;
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
    public String lista(@RequestParam(required = false) String szukaj, Model model) {
        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("skierowania", skierowanieRepo.szukajSkierowan(szukaj));
        } else {
            model.addAttribute("skierowania", skierowanieRepo.findAll());
        }
        return "skierowania/lista";
    }

    @GetMapping("/nowe")
    public String formularz(@RequestParam Long idWizyty, Model model) {
        Wizyta w = wizytaRepo.findById(idWizyty).orElseThrow();
        Skierowanie s = new Skierowanie();
        s.setWizyta(w);
        s.setPacjent(w.getPacjent());
        s.setLekarz(w.getLekarz());
        s.setDataWystawienia(LocalDate.now());

        model.addAttribute("skierowanie", s);
        return "skierowania/formularz";
    }

    @PostMapping("/zapisz")
    public String zapisz(@Valid @ModelAttribute("skierowanie") Skierowanie s,
                         BindingResult result, RedirectAttributes ra) {

        if (skierowanieRepo.existsByKodDokumentuAndPacjent_Pesel(s.getKodDokumentu(), s.getPacjent().getPesel())) {
            result.rejectValue("kodDokumentu", "error.skierowanie", "Ten kod dokumentu jest już zajęty dla tego pacjenta.");
        }

        if (result.hasErrors()) {
            return "skierowania/formularz";
        }

        skierowanieRepo.save(s);
        ra.addFlashAttribute("success", "Wystawiono skierowanie pomyślnie.");
        return "redirect:/skierowania";
    }
}