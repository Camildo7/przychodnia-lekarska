package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Wyposazenie;

public interface WyposazenieRepository extends JpaRepository<Wyposazenie, String> {}