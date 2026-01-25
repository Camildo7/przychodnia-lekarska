package pl.przychodnia.app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.dto.HistoriaWizytDTO;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PacjentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void dodajPacjenta(String pesel, String imie, String nazwisko, String adres, String telefon) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.dodaj_pacjenta");

        query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_imie", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nazwisko", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_adres", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_telefon", String.class, ParameterMode.IN);

        query.setParameter("p_pesel", pesel);
        query.setParameter("p_imie", imie);
        query.setParameter("p_nazwisko", nazwisko);
        query.setParameter("p_adres", adres);
        query.setParameter("p_telefon", telefon);

        query.execute();
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<HistoriaWizytDTO> pobierzHistorie(String pesel) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.pobierz_historie_pacjenta");

            query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_kursor", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_pesel", pesel);
            query.execute();

            List<Object[]> results = query.getResultList();
            List<HistoriaWizytDTO> historia = new ArrayList<>();

            // Formatery daty
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Object[] row : results) {
                Object rawDate = row[0];
                String dataStr = "Brak daty";

                // PANCERNA KONWERSJA DATY
                if (rawDate != null) {
                    try {
                        if (rawDate instanceof Timestamp) {
                            dataStr = sdf.format((Timestamp) rawDate);
                        } else if (rawDate instanceof java.sql.Date) {
                            dataStr = sdf.format((java.sql.Date) rawDate);
                        } else if (rawDate instanceof java.util.Date) {
                            dataStr = sdf.format((java.util.Date) rawDate);
                        } else if (rawDate instanceof LocalDateTime) {
                            dataStr = ((LocalDateTime) rawDate).format(dtf);
                        } else {
                            // Jeśli to coś innego, np. String lub Oracle TIMESTAMP
                            dataStr = rawDate.toString();
                        }
                    } catch (Exception e) {
                        dataStr = "Błąd daty";
                        System.err.println("Błąd formatowania: " + rawDate.getClass().getName());
                    }
                }

                historia.add(new HistoriaWizytDTO(
                        dataStr,            // Teraz przekazujemy gotowy String
                        (String) row[1],    // Lekarz
                        (String) row[2],    // Choroba
                        (String) row[3]     // Zalecenia
                ));
            }

            return historia;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Zwróć pustą listę zamiast błędu 500
        }
    }
}