package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.PozycjaRecepty;
import pl.przychodnia.app.entity.PozycjaReceptyId;
import pl.przychodnia.app.entity.Recepta;
import pl.przychodnia.app.entity.ReceptaId;
import pl.przychodnia.app.repository.*;
import pl.przychodnia.app.service.ReceptaService;

import java.time.LocalDate;
// UWAGA: UUID już niepotrzebne

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

    // --- LISTA ---
    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj, Model model) {
        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("recepty", receptaRepo.szukajRecept(szukaj));
        } else {
            model.addAttribute("recepty", receptaRepo.findAll());
        }
        return "recepty/lista";
    }

    // --- TWORZENIE NAGŁÓWKA ---
    @GetMapping("/nowa")
    public String formularz(Model model) {
        model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
        return "recepty/nowa";
    }

    @PostMapping("/utworz")
    public String utworz(@RequestParam Long idWizyty,
                         @RequestParam String kodDokumentu, // Pobieramy kod z inputa
                         Model model, // Używamy Model zamiast RedirectAttributes w przypadku błędu, żeby łatwo wrócić
                         RedirectAttributes ra) {
        try {
            var wizyta = wizytaRepo.findById(idWizyty).orElseThrow();
            String pesel = wizyta.getPacjent().getPesel();

            // 1. Walidacja formatu (4 cyfry)
            if (!kodDokumentu.matches("\\d{4}")) {
                throw new IllegalArgumentException("Kod recepty musi składać się dokładnie z 4 cyfr!");
            }

            // 2. Walidacja unikalności DLA TEGO PACJENTA
            if (receptaRepo.existsByKodDokumentuAndPacjent_Pesel(kodDokumentu, pesel)) {
                throw new IllegalArgumentException("Ten pacjent (PESEL: " + pesel + ") posiada już receptę o kodzie " + kodDokumentu);
            }

            // 3. Wywołanie procedury
            receptaService.utworzRecepte(kodDokumentu, pesel, wizyta.getLekarz().getNumerPwz(), idWizyty);

            ra.addAttribute("kod", kodDokumentu);
            ra.addAttribute("pesel", pesel);
            return "redirect:/recepty/szczegoly";

        } catch (Exception e) {
            // W razie błędu wracamy do formularza z komunikatem
            model.addAttribute("error", e.getMessage());
            // Musimy ponownie załadować listę wizyt, bo wracamy do widoku "nowa"
            model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
            return "recepty/nowa";
        }
    }

    // --- SZCZEGÓŁY / MASTER-DETAIL ---
    @GetMapping("/szczegoly")
    public String szczegoly(@RequestParam String kod, @RequestParam String pesel, Model model) {
        ReceptaId id = new ReceptaId(kod, pesel);
        Recepta recepta = receptaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono recepty"));

        model.addAttribute("recepta", recepta);
        model.addAttribute("pozycje", pozycjaRepo.findByKodDokumentuAndPesel(kod, pesel));
        model.addAttribute("leki", lekRepo.findAll());
        return "recepty/szczegoly";
    }

    // --- DODAWANIE POZYCJI ---
    @PostMapping("/dodaj-pozycje")
    public String dodajPozycje(@RequestParam String kodDok, @RequestParam String pesel,
                               @RequestParam String kodLeku, @RequestParam int ilosc,
                               @RequestParam String dawkowanie, RedirectAttributes ra) {
        try {
            receptaService.dodajPozycje(kodDok, pesel, kodLeku, ilosc, dawkowanie);
            ra.addFlashAttribute("success", "Dodano lek.");
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("ORA-20011")) {
                ra.addFlashAttribute("error", "Brak wystarczającej ilości leku w magazynie!");
            } else {
                ra.addFlashAttribute("error", "Błąd dodawania pozycji.");
            }
        }
        ra.addAttribute("kod", kodDok);
        ra.addAttribute("pesel", pesel);
        return "redirect:/recepty/szczegoly";
    }

    // --- USUWANIE CAŁEJ RECEPTY ---
    @GetMapping("/usun")
    public String usunRecepte(@RequestParam String kod, @RequestParam String pesel, RedirectAttributes ra) {
        try {
            receptaService.usunRecepte(kod, pesel);
            ra.addFlashAttribute("success", "Recepta została usunięta.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie można usunąć recepty.");
        }
        return "redirect:/recepty";
    }

    // --- USUWANIE POZYCJI ---
    @GetMapping("/usun-pozycje")
    public String usunPozycje(@RequestParam String kod, @RequestParam String pesel, @RequestParam String ean, RedirectAttributes ra) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
        pozycjaRepo.deleteById(id);

        ra.addAttribute("kod", kod);
        ra.addAttribute("pesel", pesel);
        ra.addFlashAttribute("success", "Usunięto lek z recepty.");
        return "redirect:/recepty/szczegoly";
    }

    // --- EDYCJA NAGŁÓWKA ---
    @GetMapping("/edytuj")
    public String edytujRecepte(@RequestParam String kod, @RequestParam String pesel, Model model) {
        try {
            ReceptaId id = new ReceptaId(kod, pesel);
            Recepta recepta = receptaRepo.findById(id).orElseThrow();
            model.addAttribute("recepta", recepta);
            return "recepty/edytuj";
        } catch (Exception e) {
            return "redirect:/recepty?error=Błąd odczytu recepty";
        }
    }

    @PostMapping("/zapisz")
    public String zapiszRecepte(@RequestParam String kodDokumentu,
                                @RequestParam String pesel,
                                @RequestParam String dataWaznosci) {
        ReceptaId id = new ReceptaId(kodDokumentu, pesel);
        Recepta recepta = receptaRepo.findById(id).orElseThrow();
        recepta.setDataWaznosci(LocalDate.parse(dataWaznosci));
        receptaRepo.save(recepta);
        return "redirect:/recepty/szczegoly?kod=" + kodDokumentu + "&pesel=" + pesel;
    }

    // --- EDYCJA POZYCJI ---
    @GetMapping("/edytuj-pozycje")
    public String edytujPozycje(@RequestParam String kod, @RequestParam String pesel, @RequestParam String ean, Model model) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
        PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow();
        model.addAttribute("pozycja", pozycja);
        return "recepty/edytuj_pozycje";
    }

    @PostMapping("/zapisz-pozycje")
    public String zapiszPozycje(@RequestParam String kod, @RequestParam String pesel, @RequestParam String ean,
                                @RequestParam Integer ilosc, @RequestParam String dawkowanie, RedirectAttributes ra) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
        PozycjaRecepty p = pozycjaRepo.findById(id).orElseThrow();
        p.setIloscOpakowan(ilosc);
        p.setDawkowanie(dawkowanie);
        pozycjaRepo.save(p);

        ra.addAttribute("kod", kod);
        ra.addAttribute("pesel", pesel);
        return "redirect:/recepty/szczegoly";
    }
}