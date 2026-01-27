package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Gabinet;
import java.util.List;

public interface GabinetRepository extends JpaRepository<Gabinet, Long> {
    // Szukanie po opisie funkcji (np. "Zabiegowy")
    List<Gabinet> findByOpisFunkcjiContainingIgnoreCase(String opis);
}