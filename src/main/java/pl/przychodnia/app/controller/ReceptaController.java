package pl.przychodnia.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.PozycjaRecepty;
import pl.przychodnia.app.entity.PozycjaReceptyId;
import pl.przychodnia.app.entity.Recepta;
import pl.przychodnia.app.entity.ReceptaId;
import pl.przychodnia.app.entity.Wizyta;
import pl.przychodnia.app.repository.*;
import pl.przychodnia.app.service.ReceptaService;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

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
    public String lista(@RequestParam(required = false) String szukaj,
                        @RequestParam(defaultValue = "dataWystawienia") String sortField,
                        @RequestParam(defaultValue = "desc") String sortDir,
                        Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);

        if (szukaj != null && !szukaj.isEmpty()) {
            model.addAttribute("recepty", receptaRepo.szukajRecept(szukaj, sort));
        } else {
            model.addAttribute("recepty", receptaRepo.findAll(sort));
        }

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "recepty/lista";
    }

    // --- TWORZENIE NAGŁÓWKA ---
    @GetMapping("/nowa")
    public String formularz(@RequestParam(required = false) Long idWizyty, Model model) {
        if (idWizyty != null) {
            // połączenie z konkretną wizytą
            Wizyta wizyta = wizytaRepo.findById(idWizyty).orElseThrow();
            model.addAttribute("wybranaWizyta", wizyta);
        } else {
            // lista wizyt do wyboru
            model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
        }
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
            }
            else if (msg != null && msg.contains("ORA-00001")) {
                ra.addFlashAttribute("error", "Ten lek znajduje się już na recepcie!");
            }
            else {
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
    public String usunPozycje(@RequestParam String kod,
                              @RequestParam String pesel,
                              @RequestParam String ean,
                              RedirectAttributes ra) {

        // usunięcie pozycji z recepty z przywróceniem stanu magazynowego leku
        receptaService.usunPozycjeZPrzywroceniemStanu(ean, pesel, kod);

        // przekierowanie z komunikatem
        ra.addAttribute("kod", kod);
        ra.addAttribute("pesel", pesel);
        ra.addFlashAttribute("success", "Usunięto lek z recepty (stan magazynowy został przywrócony).");

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
                                @RequestParam String dataWaznosci,
                                Model model) {

        ReceptaId id = new ReceptaId(kodDokumentu, pesel);
        Recepta recepta = receptaRepo.findById(id).orElseThrow();

        LocalDate nowaDataWaznosci = LocalDate.parse(dataWaznosci);

        // walidacja czy dara ważności nie jest wcześniejsza niż data wystawienia
        if (nowaDataWaznosci.isBefore(recepta.getDataWystawienia())) {

            model.addAttribute("error", "Data ważności (" + nowaDataWaznosci + ") " +
                    "nie może być wcześniejsza niż data wystawienia (" + recepta.getDataWystawienia() + ").");

            model.addAttribute("recepta", recepta);

            return "recepty/edytuj";
        }

        recepta.setDataWaznosci(nowaDataWaznosci);
        receptaRepo.save(recepta);

        return "redirect:/recepty/szczegoly?kod=" + kodDokumentu + "&pesel=" + pesel;
    }



    // --- EDYCJA POZYCJI (Widok formularza) ---
    @GetMapping("/edytuj-pozycje")
    public String edytujPozycje(@RequestParam String kod,
                                @RequestParam String pesel,
                                @RequestParam String ean,
                                Model model) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
        PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow();

        // LOGIKA LIMITU:
        // To co jest w magazynie + To co "trzymamy" w tej pozycji
        int stanMagazynowy = pozycja.getLek().getStanMagazynowy();
        int aktualniePrzypisane = pozycja.getIloscOpakowan();
        int maxIlosc = stanMagazynowy + aktualniePrzypisane;

        model.addAttribute("pozycja", pozycja);
        model.addAttribute("maxIlosc", maxIlosc);
        model.addAttribute("stanMagazynowy", stanMagazynowy); // Do wyświetlenia informacji

        return "recepty/edytuj_pozycje";
    }

    // --- ZAPIS POZYCJI (Przetwarzanie formularza) ---
    @PostMapping("/zapisz-pozycje")
    public String zapiszPozycje(@RequestParam String kod,
                                @RequestParam String pesel,
                                @RequestParam String ean,
                                @RequestParam Integer ilosc,
                                @RequestParam String dawkowanie,
                                RedirectAttributes ra,
                                Model model) {
        try {
            // Próba zapisu przez serwis
            receptaService.edytujPozycjeZWalidacjaStanu(ean, pesel, kod, ilosc, dawkowanie);

            ra.addAttribute("kod", kod);
            ra.addAttribute("pesel", pesel);
            ra.addFlashAttribute("success", "Zaktualizowano pozycję.");
            return "redirect:/recepty/szczegoly";

        } catch (IllegalArgumentException e) {
            // W razie błędu (ktoś próbował obejść zabezpieczenia):
            model.addAttribute("error", e.getMessage());

            // Musimy ponownie załadować dane do widoku, żeby formularz nie był pusty
            PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
            PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow();

            int stanMagazynowy = pozycja.getLek().getStanMagazynowy();
            int maxIlosc = stanMagazynowy + pozycja.getIloscOpakowan();

            model.addAttribute("pozycja", pozycja);
            model.addAttribute("maxIlosc", maxIlosc);
            model.addAttribute("stanMagazynowy", stanMagazynowy);

            return "recepty/edytuj_pozycje";
        }
    }
}