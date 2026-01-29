package pl.przychodnia.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.przychodnia.app.entity.Lek;

public interface LekRepository extends JpaRepository<Lek, String> {

    @Query("SELECT l FROM Lek l WHERE " +
            "LOWER(l.nazwaHandlowa) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(l.substancjaCzynna) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "l.kodEan LIKE CONCAT('%', :szukaj, '%')")
    Page<Lek> szukajLekow(@Param("szukaj") String szukaj, Pageable pageable);
}