package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Gabinet;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GabinetRepository extends JpaRepository<Gabinet, Long> {
    // sszukanie po opisie funkcji
    Page<Gabinet> findByOpisFunkcjiContainingIgnoreCase(String opis, Pageable pageable);
}