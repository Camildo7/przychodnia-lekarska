package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Lekarz;
import java.util.List;

public interface LekarzRepository extends JpaRepository<Lekarz, String> {
    List<Lekarz> findByNazwiskoContainingIgnoreCase(String nazwisko);
}