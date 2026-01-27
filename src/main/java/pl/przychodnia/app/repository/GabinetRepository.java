package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Gabinet;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface GabinetRepository extends JpaRepository<Gabinet, Long> {
    // sszukanie po opisie funkcji
    List<Gabinet> findByOpisFunkcjiContainingIgnoreCase(String opis, Sort sort);
}