package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Gabinet;
import pl.przychodnia.app.entity.Specjalizacja;
import pl.przychodnia.app.repository.GabinetRepository;
import pl.przychodnia.app.repository.SpecjalizacjaRepository;

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
    public String listaGabinetow(Model model) {
        model.addAttribute("gabinety", gabinetRepo.findAll());
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
    public String zapiszGabinet(Gabinet g) {
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
    public String listaSpecjalizacji(Model model) {
        model.addAttribute("specjalizacje", specRepo.findAll());
        return "specjalizacje/lista";
    }

    @GetMapping("/specjalizacje/nowa")
    public String nowaSpecjalizacja(Model model) {
        model.addAttribute("specjalizacja", new Specjalizacja());
        return "specjalizacje/formularz";
    }

    @GetMapping("/specjalizacje/edytuj/{id}")
    public String edytujSpecjalizacje(@PathVariable String id, Model model) {
        Specjalizacja s = specRepo.findById(id).orElseThrow();
        model.addAttribute("specjalizacja", s);
        return "specjalizacje/formularz";
    }

    @PostMapping("/specjalizacje/zapisz")
    public String zapiszSpecjalizacje(Specjalizacja s) {
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