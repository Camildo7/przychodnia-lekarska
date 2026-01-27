package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.Choroba;
import pl.przychodnia.app.entity.Wizyta;
import pl.przychodnia.app.repository.*;
import pl.przychodnia.app.service.WizytaService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class WizytaController {

    private final WizytaRepository wizytaRepo;
    private final LekarzRepository lekarzRepo;
    private final PacjentRepository pacjentRepo;
    private final GabinetRepository gabinetRepo;
    private final ChorobaRepository chorobaRepo; // Nowe repo
    private final WizytaService wizytaService;

    public WizytaController(WizytaRepository w, LekarzRepository l, PacjentRepository p,
                            GabinetRepository g, ChorobaRepository c, WizytaService ws) {
        this.wizytaRepo = w;
        this.lekarzRepo = l;
        this.pacjentRepo = p;
        this.gabinetRepo = g;
        this.chorobaRepo = c;
        this.wizytaService = ws;
    }

    // --- KALENDARZ ---
    @GetMapping("/kalendarz")
    public String widokKalendarza(Model model) {
        model.addAttribute("lekarze", lekarzRepo.findAll());
        return "wizyty/kalendarz";
    }

    // --- SZCZEGÓŁY WIZYTY ---
    @GetMapping("/wizyta/{id}")
    public String szczegolyWizyty(@PathVariable Long id, Model model) {
        Wizyta wizyta = wizytaRepo.findById(id).orElseThrow();

        model.addAttribute("w", wizyta);
        model.addAttribute("choroby", chorobaRepo.findAll()); // Lista do checkboxów
        model.addAttribute("lekarze", lekarzRepo.findAll());
        model.addAttribute("gabinety", gabinetRepo.findAll());

        return "wizyty/szczegoly";
    }

    // --- EDYCJA PARAMETRÓW ---
    @PostMapping("/wizyta/zapisz")
    public String zapiszZmiany(@RequestParam Long numerWizyty,
                               @RequestParam String dataIGodzina,
                               @RequestParam String lekarz,
                               @RequestParam Long gabinet,
                               RedirectAttributes ra) {
        try {
            wizytaService.edytujWizyte(numerWizyty, dataIGodzina, lekarz, gabinet);

            ra.addFlashAttribute("success", "Dane wizyty zostały zaktualizowane.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie udało się zapisać zmian: " + e.getMessage());
        }

        return "redirect:/wizyta/" + numerWizyty;
    }

    // --- ZAKOŃCZENIE WIZYTY ---
    @PostMapping("/wizyta/zakoncz-pelna")
    public String zakonczPelna(@RequestParam Long idWizyty,
                               @RequestParam String zalecenia,
                               // ZMIANA: Dodano (name = "choroby"), aby połączyć formularz z kodem
                               @RequestParam(name = "choroby", required = false) List<String> kodyChorob) {

        // 1. Procedura (ustawia status 'T' i zalecenia)
        wizytaService.zakonczWizyte(idWizyty, zalecenia);

        // 2. Przypisanie chorób (JPA)
        if (kodyChorob != null && !kodyChorob.isEmpty()) {
            Wizyta w = wizytaRepo.findById(idWizyty).orElseThrow();
            List<Choroba> wybraneChoroby = chorobaRepo.findAllById(kodyChorob);
            w.setChoroby(wybraneChoroby);
            wizytaRepo.save(w);
        }

        return "redirect:/wizyta/" + idWizyty;
    }

    // --- STARE METODY (Żeby nie psuć reszty) ---
    @GetMapping("/")
    public String listaWizyt(Model model) {
        return "redirect:/kalendarz"; // Przekierujmy od razu na kalendarz
    }

    @GetMapping("/nowa")
    public String formularzWizyty(Model model) {
        model.addAttribute("lekarze", lekarzRepo.findAll());
        model.addAttribute("pacjenci", pacjentRepo.findAll());
        model.addAttribute("gabinety", gabinetRepo.findAll());
        return "nowa_wizyta";
    }

    @PostMapping("/nowa")
    public String utworzWizyte(@RequestParam String pesel,
                               @RequestParam String lekarzPwz,
                               @RequestParam Long idGabinetu,
                               @RequestParam String data,
                               Model model) {
        try {
            wizytaService.zarejestrujWizyte(lekarzPwz, pesel, idGabinetu, LocalDateTime.parse(data));
            return "redirect:/kalendarz";
        } catch (Exception e) {
            model.addAttribute("error", "Błąd: " + e.getMessage());

            model.addAttribute("wybranyPesel", pesel);
            model.addAttribute("wybranyLekarz", lekarzPwz);
            model.addAttribute("wybranyGabinet", idGabinetu);
            model.addAttribute("wybranaData", data);

            model.addAttribute("lekarze", lekarzRepo.findAll());
            model.addAttribute("pacjenci", pacjentRepo.findAll());
            model.addAttribute("gabinety", gabinetRepo.findAll());

            return "nowa_wizyta";
        }
    }

    @GetMapping("/wizyta/usun/{id}")
    public String usunWizyte(@PathVariable Long id, RedirectAttributes ra) {
        try {
            wizytaService.usunWizyte(id);
            ra.addFlashAttribute("success", "Wizyta została pomyślnie usunięta.");
            return "redirect:/kalendarz";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie można usunąć wizyty! Jest do niej przypisane skierowanie lub recepta.");
            return "redirect:/wizyta/" + id;
        }
    }
}