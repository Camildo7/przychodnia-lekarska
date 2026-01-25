package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Lekarz;

public interface LekarzRepository extends JpaRepository<Lekarz, String> {
}