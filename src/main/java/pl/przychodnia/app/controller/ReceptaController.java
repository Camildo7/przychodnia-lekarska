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
import pl.przychodnia.app.validation.MaxBytes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @GetMapping
    public String lista(@RequestParam(required = false) String szukaj,
                        @RequestParam(defaultValue = "0") int page,   // numer strony
                        @RequestParam(defaultValue = "10") int size,  // liczba elementów na stronie
                        @RequestParam(defaultValue = "dataWystawienia") String sortField,
                        @RequestParam(defaultValue = "desc") String sortDir,
                        Model model) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Recepta> pageRecept;

        if (szukaj != null && !szukaj.isEmpty()) {
            pageRecept = receptaRepo.szukajRecept(szukaj, pageable);
        } else {
            pageRecept = receptaRepo.findAll(pageable);
        }

        model.addAttribute("recepty", pageRecept);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("szukaj", szukaj);

        return "recepty/lista";
    }

    @GetMapping("/nowa")
    public String formularz(@RequestParam(required = false) Long idWizyty,
                            @RequestParam(defaultValue = "recepty") String source,
                            Model model) {
        if (idWizyty != null) {
            Wizyta wizyta = wizytaRepo.findById(idWizyty).orElseThrow();
            model.addAttribute("wybranaWizyta", wizyta);
        } else {
            model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
        }
        model.addAttribute("source", source);
        return "recepty/nowa";
    }

    @PostMapping("/utworz")
    public String utworz(@RequestParam Long idWizyty,
                         @RequestParam String kodDokumentu,
                         @RequestParam(defaultValue = "recepty") String source,
                         Model model,
                         RedirectAttributes ra) {
        try {
            var wizyta = wizytaRepo.findById(idWizyty).orElseThrow();
            String pesel = wizyta.getPacjent().getPesel();

            if (!kodDokumentu.matches("\\d{4}")) {
                throw new IllegalArgumentException("Kod recepty musi składać się dokładnie z 4 cyfr!");
            }

            if (receptaRepo.existsByKodDokumentuAndPacjent_Pesel(kodDokumentu, pesel)) {
                throw new IllegalArgumentException("Ten pacjent posiada już receptę o kodzie " + kodDokumentu);
            }

            receptaService.utworzRecepte(kodDokumentu, pesel, wizyta.getLekarz().getNumerPwz(), idWizyty);

            ra.addAttribute("kod", kodDokumentu);
            ra.addAttribute("pesel", pesel);

            if ("wizyta".equals(source)) {
                ra.addAttribute("source", "wizyta");
            }

            return "redirect:/recepty/szczegoly";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("wizyty", wizytaRepo.findAllByOrderByDataIGodzinaDesc());
            model.addAttribute("source", source);
            return "recepty/nowa";
        }
    }

    @GetMapping("/szczegoly")
    public String szczegoly(@RequestParam String kod, @RequestParam String pesel, Model model) {
        ReceptaId id = new ReceptaId(kod, pesel);
        Recepta recepta = receptaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono recepty"));

        model.addAttribute("recepta", recepta);
        model.addAttribute("pozycje", pozycjaRepo.findByKodDokumentuAndPesel(kod, pesel));
        model.addAttribute("leki", lekRepo.findAll(Sort.by("nazwaHandlowa")));

        DodajPozycjeForm form = new DodajPozycjeForm();
        form.setKodDok(kod);
        form.setPesel(pesel);
        model.addAttribute("nowaPozycja", form);

        return "recepty/szczegoly";
    }

    @PostMapping("/dodaj-pozycje")
    public String dodajPozycje(@jakarta.validation.Valid @ModelAttribute("nowaPozycja") DodajPozycjeForm form,
                               org.springframework.validation.BindingResult result,
                               Model model,
                               RedirectAttributes ra) {

        if (result.hasErrors()) {
            ReceptaId id = new ReceptaId(form.getKodDok(), form.getPesel());
            Recepta r = receptaRepo.findById(id).orElseThrow();

            model.addAttribute("recepta", r);
            model.addAttribute("pozycje", pozycjaRepo.findByKodDokumentuAndPesel(form.getKodDok(), form.getPesel()));
            model.addAttribute("leki", lekRepo.findAll(Sort.by("nazwaHandlowa")));

            return "recepty/szczegoly";
        }

        try {
            receptaService.dodajPozycje(
                    form.getKodDok(),
                    form.getPesel(),
                    form.getKodLeku(),
                    form.getIlosc(),
                    form.getDawkowanie()
            );

            ra.addAttribute("kod", form.getKodDok());
            ra.addAttribute("pesel", form.getPesel());
            ra.addFlashAttribute("success", "Dodano lek do recepty.");
            return "redirect:/recepty/szczegoly";

        } catch (Exception e) {
            ra.addAttribute("kod", form.getKodDok());
            ra.addAttribute("pesel", form.getPesel());

            String msg = e.getMessage();
            if (msg != null && msg.contains("ORA-20011")) {
                ra.addFlashAttribute("error", "Brak wystarczającej ilości leku w magazynie!");
            } else if (msg != null && msg.contains("ORA-00001")) {
                ra.addFlashAttribute("error", "Ten lek znajduje się już na recepcie!");
            } else {
                ra.addFlashAttribute("error", "Błąd dodawania: " + msg);
            }
            return "redirect:/recepty/szczegoly";
        }
    }

    @GetMapping("/usun")
    public String usunRecepte(@RequestParam String kod,
                              @RequestParam String pesel,
                              @RequestParam(defaultValue = "recepty") String source,
                              RedirectAttributes ra) {
        try {
            ReceptaId id = new ReceptaId(kod, pesel);
            Recepta r = receptaRepo.findById(id).orElse(null);
            Long idWizyty = (r != null && r.getWizyta() != null) ? r.getWizyta().getNumerWizyty() : null;

            receptaService.usunRecepte(kod, pesel);
            ra.addFlashAttribute("success", "Recepta została usunięta.");

            if ("wizyta".equals(source) && idWizyty != null) {
                return "redirect:/wizyta/" + idWizyty;
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie można usunąć recepty.");
        }
        return "redirect:/recepty";
    }

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

    @GetMapping("/edytuj-pozycje")
    public String edytujPozycje(@RequestParam String kod,
                                @RequestParam String pesel,
                                @RequestParam String ean,
                                Model model) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
        PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow();

        int stanMagazynowy = pozycja.getLek().getStanMagazynowy();
        int aktualniePrzypisane = pozycja.getIloscOpakowan();
        int maxIlosc = stanMagazynowy + aktualniePrzypisane;

        model.addAttribute("pozycja", pozycja);
        model.addAttribute("maxIlosc", maxIlosc);
        model.addAttribute("stanMagazynowy", stanMagazynowy);

        EdytujPozycjeForm form = new EdytujPozycjeForm();
        form.setKod(kod);
        form.setPesel(pesel);
        form.setEan(ean);
        form.setIlosc(pozycja.getIloscOpakowan());
        form.setDawkowanie(pozycja.getDawkowanie());

        model.addAttribute("edycjaPozycji", form);

        return "recepty/edytuj_pozycje";
    }

    @PostMapping("/zapisz-pozycje")
    public String zapiszPozycje(@jakarta.validation.Valid @ModelAttribute("edycjaPozycji") EdytujPozycjeForm form,
                                org.springframework.validation.BindingResult result,
                                RedirectAttributes ra,
                                Model model) {

        // walidacja formularza
        if (result.hasErrors()) {
            PozycjaReceptyId id = new PozycjaReceptyId(form.getEan(), form.getPesel(), form.getKod());
            PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow();

            int stanMagazynowy = pozycja.getLek().getStanMagazynowy();
            int maxIlosc = stanMagazynowy + pozycja.getIloscOpakowan();

            model.addAttribute("pozycja", pozycja);
            model.addAttribute("maxIlosc", maxIlosc);
            model.addAttribute("stanMagazynowy", stanMagazynowy);

            return "recepty/edytuj_pozycje";
        }

        try {
            receptaService.edytujPozycjeZWalidacjaStanu(
                    form.getEan(),
                    form.getPesel(),
                    form.getKod(),
                    form.getIlosc(),
                    form.getDawkowanie()
            );

            ra.addAttribute("kod", form.getKod());
            ra.addAttribute("pesel", form.getPesel());
            ra.addFlashAttribute("success", "Zaktualizowano pozycję.");
            return "redirect:/recepty/szczegoly";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

            PozycjaReceptyId id = new PozycjaReceptyId(form.getEan(), form.getPesel(), form.getKod());
            PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow();

            int stanMagazynowy = pozycja.getLek().getStanMagazynowy();
            int maxIlosc = stanMagazynowy + pozycja.getIloscOpakowan();

            model.addAttribute("pozycja", pozycja);
            model.addAttribute("maxIlosc", maxIlosc);
            model.addAttribute("stanMagazynowy", stanMagazynowy);

            return "recepty/edytuj_pozycje";
        }
    }

    @lombok.Data
    class EdytujPozycjeForm {
        private String kod;
        private String pesel;
        private String ean;

        @jakarta.validation.constraints.NotNull(message = "Ilość jest wymagana.")
        @jakarta.validation.constraints.Min(value = 1, message = "Ilość musi być większa od 0.")
        private Integer ilosc;

        @jakarta.validation.constraints.NotBlank(message = "Dawkowanie nie może być puste.")
        @jakarta.validation.constraints.Size(max = 100, message = "Opis dawkowania za długi (max 100 znaków).")
        @MaxBytes(value = 100, message = "Opis za długi (max 100 bajtów - polskie znaki liczone x2)")
        private String dawkowanie;
    }

    @lombok.Data
    class DodajPozycjeForm {
        private String kodDok;
        private String pesel;

        @jakarta.validation.constraints.NotBlank(message = "Musisz wybrać lek z listy.")
        private String kodLeku;

        @jakarta.validation.constraints.NotNull(message = "Podaj ilość.")
        @jakarta.validation.constraints.Min(value = 1, message = "Ilość musi być większa od 0.")
        @jakarta.validation.constraints.Max(value = 50, message = "Maksymalnie 50 opakowań.")
        private Integer ilosc = 1;

        @jakarta.validation.constraints.NotBlank(message = "Dawkowanie jest wymagane.")
        @jakarta.validation.constraints.Size(max = 100, message = "Dawkowanie za długie (max 100 znaków).")
        @MaxBytes(value = 100, message = "Dawkowanie za długie (max 100 znaków - polskie znaki liczone x2)")
        private String dawkowanie;
    }
}