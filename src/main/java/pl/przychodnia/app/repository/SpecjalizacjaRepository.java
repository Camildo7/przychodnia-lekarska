package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Specjalizacja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecjalizacjaRepository extends JpaRepository<Specjalizacja, String> {

    Page<Specjalizacja> findByNazwaSpecjalizacjiContainingIgnoreCase(String nazwa, Pageable pageable);
}