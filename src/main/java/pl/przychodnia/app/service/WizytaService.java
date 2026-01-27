package pl.przychodnia.app.service;

import pl.przychodnia.app.repository.WizytaRepository;
import pl.przychodnia.app.entity.Wizyta;
import pl.przychodnia.app.repository.GabinetRepository;
import pl.przychodnia.app.repository.LekarzRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class WizytaService {

    @PersistenceContext
    private EntityManager entityManager;

    private final WizytaRepository wizytaRepo;
    private final LekarzRepository lekarzRepo;
    private final GabinetRepository gabinetRepo;

    public WizytaService(WizytaRepository wizytaRepo, LekarzRepository lekarzRepo, GabinetRepository gabinetRepo) {
        this.wizytaRepo = wizytaRepo;
        this.lekarzRepo = lekarzRepo;
        this.gabinetRepo = gabinetRepo;
    }

    @Transactional
    public void zarejestrujWizyte(String pwzLekarza, String pesel, Long nrGabinetu, LocalDateTime data) {

        LocalDateTime startMinus30 = data.minusMinutes(30);
        LocalDateTime startPlus30 = data.plusMinutes(30);

        // Sprawdzanie dyspozycji lekarza
        long konfliktyLekarz = wizytaRepo.countKonfliktyLekarza(pwzLekarza, startMinus30, startPlus30);
        if (konfliktyLekarz > 0) {
            throw new IllegalStateException("Ten lekarz ma już umówioną wizytę w tym czasie.");
        }

        // Sprawdzanie dyspozycji lekarza
        long konfliktyPacjent = wizytaRepo.countKonfliktyPacjenta(pesel, startMinus30, startPlus30);
        if (konfliktyPacjent > 0) {
            throw new IllegalStateException("Ten pacjent ma już umówioną wizytę w tym czasie.");
        }

        // Sprawdzanie dostępności gabinetu
        long konfliktyGabinet = wizytaRepo.countKonfliktyGabinetu(nrGabinetu, startMinus30, startPlus30);
        if (konfliktyGabinet > 0) {
            throw new IllegalStateException("Ten gabinet jest zajęty w wybranym terminie.");
        }


        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.utworz_wizyte");

        query.registerStoredProcedureParameter("p_nr_lekarza", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nr_gabinetu", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_data_godzina", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nr_wizyty", Long.class, ParameterMode.OUT);

        query.setParameter("p_nr_lekarza", pwzLekarza);
        query.setParameter("p_pesel", pesel);
        query.setParameter("p_nr_gabinetu", nrGabinetu);
        query.setParameter("p_data_godzina", data);

        query.execute();
    }

    @Transactional
    public void zakonczWizyte(Long idWizyty, String zalecenia) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.zakoncz_wizyte");
        query.registerStoredProcedureParameter("p_nr_wizyty", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_zalecenia", String.class, ParameterMode.IN);

        query.setParameter("p_nr_wizyty", idWizyty);
        query.setParameter("p_zalecenia", zalecenia);

        query.execute();
    }

    @Transactional
    public void edytujWizyte(Long idWizyty, String nowaDataStr, String nowyLekarzPwz, Long nowyGabinetId) {

        Wizyta wizyta = wizytaRepo.findById(idWizyty).orElseThrow();
        LocalDateTime nowaData = LocalDateTime.parse(nowaDataStr);
        String pesel = wizyta.getPacjent().getPesel();

        LocalDateTime startMinus30 = nowaData.minusMinutes(30);
        LocalDateTime startPlus30 = nowaData.plusMinutes(30);

        long konfliktyLekarz = wizytaRepo.countKonfliktyLekarzaEdycja(nowyLekarzPwz, idWizyty, startMinus30, startPlus30);
        if (konfliktyLekarz > 0) {
            throw new IllegalStateException("Ten lekarz ma już inną wizytę w tym czasie.");
        }

        long konfliktyPacjent = wizytaRepo.countKonfliktyPacjentaEdycja(pesel, idWizyty, startMinus30, startPlus30);
        if (konfliktyPacjent > 0) {
            throw new IllegalStateException("Ten pacjent ma już inną wizytę w tym czasie.");
        }

        long konfliktyGabinet = wizytaRepo.countKonfliktyGabinetuEdycja(nowyGabinetId, idWizyty, startMinus30, startPlus30);
        if (konfliktyGabinet > 0) {
            throw new IllegalStateException("Ten gabinet jest zajęty w wybranym terminie.");
        }

        wizyta.setDataIGodzina(nowaData);
        wizyta.setLekarz(lekarzRepo.findById(nowyLekarzPwz).orElseThrow());
        wizyta.setGabinet(gabinetRepo.findById(nowyGabinetId).orElseThrow());

        wizytaRepo.save(wizyta);
    }

    @Transactional
    public void usunWizyte(Long idWizyty) {
        wizytaRepo.deleteById(idWizyty);
    }
}