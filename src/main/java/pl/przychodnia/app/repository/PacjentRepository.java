package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Pacjent;
import java.util.List;

public interface PacjentRepository extends JpaRepository<Pacjent, String> {

    // Tej metody brakowało, a kontroler próbuje jej użyć do wyszukiwarki:
    List<Pacjent> findByNazwiskoContainingIgnoreCase(String nazwisko);
}