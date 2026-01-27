package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Wyposazenie;
import java.util.List;

public interface WyposazenieRepository extends JpaRepository<Wyposazenie, String> {
    // Szukanie po nazwie sprzętu
    List<Wyposazenie> findByNazwaSprzetuContainingIgnoreCase(String nazwa);
}