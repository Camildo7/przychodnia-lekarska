package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Gabinet;

public interface GabinetRepository extends JpaRepository<Gabinet, Long> {
}