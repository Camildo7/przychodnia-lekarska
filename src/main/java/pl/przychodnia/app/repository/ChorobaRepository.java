package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Choroba;
import java.util.List;

public interface ChorobaRepository extends JpaRepository<Choroba, String> {

    // Opcjonalnie: Metoda do wyszukiwania chorób po nazwie (np. do formularza historii lub podpowiedzi)
    List<Choroba> findByNazwaChorobyContainingIgnoreCase(String nazwa);
}