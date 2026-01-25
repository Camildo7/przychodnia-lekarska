package pl.przychodnia.app.service;

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

    @Transactional
    public void zarejestrujWizyte(String pwzLekarza, String pesel, Long nrGabinetu, LocalDateTime data) {
        // Wywołujemy procedurę: obsluga_medyczna.utworz_wizyte
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.utworz_wizyte");

        // Rejestracja parametrów (zgodnie z paczka.ddl)
        query.registerStoredProcedureParameter("p_nr_lekarza", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nr_gabinetu", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_data_godzina", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nr_wizyty", Long.class, ParameterMode.OUT);

        // Ustawienie wartości
        query.setParameter("p_nr_lekarza", pwzLekarza);
        query.setParameter("p_pesel", pesel);
        query.setParameter("p_nr_gabinetu", nrGabinetu);
        query.setParameter("p_data_godzina", data);

        // Wykonanie
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
}