package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.przychodnia.app.entity.Lek;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface LekRepository extends JpaRepository<Lek, String> {

    // szukanie leków po nazwie handlowej, substancji czynnej lub kodzie EAN
    @Query("SELECT l FROM Lek l WHERE " +
            "LOWER(l.nazwaHandlowa) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(l.substancjaCzynna) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "l.kodEan LIKE CONCAT('%', :szukaj, '%')")
    List<Lek> szukajLekow(@Param("szukaj") String szukaj, Sort sort);
}