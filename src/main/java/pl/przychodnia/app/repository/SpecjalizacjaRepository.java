package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Specjalizacja;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface SpecjalizacjaRepository extends JpaRepository<Specjalizacja, String> {
    // Szukanie po nazwie specjalizacji
    List<Specjalizacja> findByNazwaSpecjalizacjiContainingIgnoreCase(String nazwa, Sort sort);
}