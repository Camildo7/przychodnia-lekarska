package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.Recepta;
import pl.przychodnia.app.entity.ReceptaId;
import pl.przychodnia.app.repository.*;
import pl.przychodnia.app.service.ReceptaService;

import java.util.UUID;

@Controller
@RequestMapping("/recepty")
public class ReceptaController {

    private final ReceptaRepository receptaRepo;
    private final PozycjaReceptyRepository pozycjaRepo;
    private final WizytaRepository wizytaRepo;
    private final LekRepository lekRepo;
    private final ReceptaService receptaService;

    public ReceptaController(ReceptaRepository rr, PozycjaReceptyRepository pr, WizytaRepository wr, LekRepository lr, ReceptaService rs) {
        this.receptaRepo = rr;
        this.pozycjaRepo = pr;
        this.wizytaRepo = wr;
        this.lekRepo = lr;
        this.receptaService = rs;
    }

    // Lista recept
    @GetMapping
    public String lista(Model model) {
        model.addAttribute("recepty", receptaRepo.findAll());
        return "recepty/lista";
    }

    // Formularz nowej recepty (Nagłówek)
    @GetMapping("/nowa")
    public String formularz(Model model) {
        // Receptę wystawiamy na podstawie wizyty - tam mamy już lekarza i pacjenta
        model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
        return "recepty/nowa";
    }

    @PostMapping("/utworz")
    public String utworz(@RequestParam Long idWizyty, RedirectAttributes ra) {
        try {
            var wizyta = wizytaRepo.findById(idWizyty).orElseThrow();
            // Generujemy unikalny kod dokumentu
            String kodDok = "REC/" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            receptaService.utworzRecepte(kodDok, wizyta.getPacjent().getPesel(), wizyta.getLekarz().getNumerPwz(), idWizyty);

            ra.addAttribute("kod", kodDok);
            ra.addAttribute("pesel", wizyta.getPacjent().getPesel());
            return "redirect:/recepty/szczegoly";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Błąd tworzenia recepty: " + e.getMessage());
            return "redirect:/recepty/nowa";
        }
    }

    // Widok Master-Detail (Recepta + Pozycje + Dodawanie leku)
    @GetMapping("/szczegoly")
    public String szczegoly(@RequestParam String kod, @RequestParam String pesel, Model model) {
        ReceptaId id = new ReceptaId(kod, pesel);
        Recepta recepta = receptaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono recepty"));

        model.addAttribute("recepta", recepta);
        model.addAttribute("pozycje", pozycjaRepo.findByKodDokumentuAndPesel(kod, pesel));
        model.addAttribute("leki", lekRepo.findAll()); // Do dropdowna przy dodawaniu pozycji

        return "recepty/szczegoly";
    }

    @PostMapping("/dodaj-pozycje")
    public String dodajPozycje(@RequestParam String kodDok,
                               @RequestParam String pesel,
                               @RequestParam String kodLeku,
                               @RequestParam int ilosc,
                               @RequestParam String dawkowanie,
                               RedirectAttributes ra) {
        try {
            receptaService.dodajPozycje(kodDok, pesel, kodLeku, ilosc, dawkowanie);
            ra.addFlashAttribute("success", "Dodano lek do recepty.");
        } catch (Exception e) {
            String msg = e.getMessage();
            String userMsg = "Błąd systemu.";

            // Obsługa użytkownika naiwnego - błąd z procedury -20011
            if (msg != null && msg.contains("ORA-20011")) {
                userMsg = "Brak wystarczającej ilości leku w magazynie! Zmniejsz ilość lub zamów towar.";
            }
            ra.addFlashAttribute("error", userMsg);
        }

        ra.addAttribute("kod", kodDok);
        ra.addAttribute("pesel", pesel);
        return "redirect:/recepty/szczegoly";
    }
}