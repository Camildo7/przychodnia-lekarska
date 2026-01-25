package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Lek;
import java.util.List;

public interface LekRepository extends JpaRepository<Lek, String> {
    // Metoda do wyszukiwarki
    List<Lek> findByNazwaHandlowaContainingIgnoreCase(String nazwa);
}