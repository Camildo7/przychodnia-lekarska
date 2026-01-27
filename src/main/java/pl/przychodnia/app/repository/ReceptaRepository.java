package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.przychodnia.app.entity.Recepta;
import pl.przychodnia.app.entity.ReceptaId;

import java.util.List;

public interface ReceptaRepository extends JpaRepository<Recepta, ReceptaId> {

    // Inteligentne wyszukiwanie (bez zmian)
    @Query("SELECT r FROM Recepta r WHERE " +
            "LOWER(r.kodDokumentu) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(r.pacjent.nazwisko) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(r.pacjent.imie) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(r.lekarz.nazwisko) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "LOWER(r.lekarz.imie) LIKE LOWER(CONCAT('%', :szukaj, '%')) OR " +
            "CAST(r.dataWystawienia AS string) LIKE CONCAT('%', :szukaj, '%')")
    List<Recepta> szukajRecept(@Param("szukaj") String szukaj);

    boolean existsByKodDokumentuAndPacjent_Pesel(String kodDokumentu, String pesel);
}