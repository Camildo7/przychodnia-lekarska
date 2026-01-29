package pl.przychodnia.app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.dto.HistoriaWizytDTO;
import pl.przychodnia.app.repository.WizytaRepository;

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

    private final WizytaRepository wizytaRepository;

    public PacjentService(WizytaRepository wizytaRepository) {
        this.wizytaRepository = wizytaRepository;
    }

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
    public List<HistoriaWizytDTO> pobierzHistorie(String pesel) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.pobierz_historie_pacjenta");

            query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_kursor", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_pesel", pesel);
            query.execute();

            List<Object[]> results = query.getResultList();
            List<HistoriaWizytDTO> historia = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Object[] row : results) {
                Object rawDate = row[0];
                String dataStr = "Brak daty";
                Long numerWizyty = null;

                LocalDateTime dataDoSzukania = null;

                if (rawDate != null) {
                    try {
                        if (rawDate instanceof java.sql.Timestamp) {
                            dataDoSzukania = ((java.sql.Timestamp) rawDate).toLocalDateTime();
                            dataStr = sdf.format((java.sql.Timestamp) rawDate);
                        }
                        else if (rawDate instanceof java.sql.Date) {
                            dataDoSzukania = ((java.sql.Date) rawDate).toLocalDate().atStartOfDay();
                            dataStr = sdf.format((java.sql.Date) rawDate);
                        }
                        else if (rawDate instanceof LocalDateTime) {
                            dataDoSzukania = (LocalDateTime) rawDate;
                            dataStr = dataDoSzukania.format(dtf);
                        }
                        else {
                            dataStr = rawDate.toString();
                        }
                    } catch (Exception e) {
                        System.err.println("Błąd konwersji daty: " + e.getMessage());
                    }
                }

                if (dataDoSzukania != null) {
                    try {
                        numerWizyty = wizytaRepository.znajdzIdWizyty(pesel, dataDoSzukania);
                    } catch (Exception e) {
                        System.err.println("Nie udało się znaleźć ID wizyty dla daty: " + dataDoSzukania);
                    }
                }

                historia.add(new HistoriaWizytDTO(
                        dataStr,
                        numerWizyty,
                        (String) row[1],
                        (String) row[2],
                        (String) row[3]
                ));
            }

            return historia;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}