package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.przychodnia.app.entity.Skierowanie;
import pl.przychodnia.app.entity.SkierowanieId;
import java.util.List;

public interface SkierowanieRepository extends JpaRepository<Skierowanie, SkierowanieId> {

    @Query("SELECT s FROM Skierowanie s WHERE " +
            "LOWER(s.kodDokumentu) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(s.pacjent.nazwisko) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(s.celBadania) LIKE LOWER(CONCAT('%', :szukaj, '%'))")
    List<Skierowanie> szukajSkierowan(@Param("szukaj") String szukaj);

    boolean existsByKodDokumentuAndPacjent_Pesel(String kodDokumentu, String pesel);
}