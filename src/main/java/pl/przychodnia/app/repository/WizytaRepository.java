package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Wizyta;
import java.util.List;

public interface WizytaRepository extends JpaRepository<Wizyta, Long> {
    // Spring sam wygeneruje zapytanie SQL na podstawie nazwy metody!
    List<Wizyta> findAllByOrderByDataIGodzinaDesc();
}