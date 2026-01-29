package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.przychodnia.app.entity.Wyposazenie;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WyposazenieRepository extends JpaRepository<Wyposazenie, String> {

    // szukanie wyposażenia po nazwie, kodzie inwentarzowym lub gabinecie
    @Query("SELECT w FROM Wyposazenie w WHERE " +
            "LOWER(w.nazwaSprzetu) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(w.kodInwentarzowy) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(w.gabinet.opisFunkcji) LIKE LOWER(CONCAT('%', :szukaj, '%'))")
    Page<Wyposazenie> szukajSprzetu(@Param("szukaj") String szukaj, Pageable pageable);
}