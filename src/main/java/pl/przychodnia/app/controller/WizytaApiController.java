package pl.przychodnia.app.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import pl.przychodnia.app.entity.Wizyta;
import pl.przychodnia.app.repository.WizytaRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wizyty")
public class  WizytaApiController {

    private final WizytaRepository wizytaRepo;

    public WizytaApiController(WizytaRepository wizytaRepo) {
        this.wizytaRepo = wizytaRepo;
    }

    @GetMapping
    public List<CalendarEvent> getEvents(@RequestParam(required = false) String lekarzId,
                                         @RequestParam(required = false) String pesel) {
        List<Wizyta> wizyty = wizytaRepo.findAll();

        if (lekarzId != null && !lekarzId.isEmpty()) {
            wizyty = wizyty.stream()
                    .filter(w -> w.getLekarz().getNumerPwz().equals(lekarzId))
                    .collect(Collectors.toList());
        }

        if (pesel != null && !pesel.isEmpty()) {
            wizyty = wizyty.stream()
                    .filter(w -> w.getPacjent().getPesel().equals(pesel))
                    .collect(Collectors.toList());
        }

        return wizyty.stream().map(w -> {
            CalendarEvent e = new CalendarEvent();
            e.setId(w.getNumerWizyty());
            e.setTitle(w.getPacjent().getNazwisko() + " " + w.getPacjent().getImie());

            e.setTitle(w.getPacjent().getNazwisko() + " " + w.getPacjent().getImie());
            e.setStart(w.getDataIGodzina().toString());
            e.setEnd(w.getDataIGodzina().plusMinutes(30).toString());

            if ("T".equals(w.getCzyOdbyta())) {
                e.setColor("#198754"); // Green
            } else {
                e.setColor("#0d6efd"); // Blue
            }
            return e;
        }).collect(Collectors.toList());
    }

    @Data
    static class CalendarEvent {
        private Long id;
        private String title;
        private String start;
        private String end;
        private String color;
    }
}