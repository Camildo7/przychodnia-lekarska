package pl.przychodnia.app.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.przychodnia.app.entity.Choroba;

import java.util.List;

public interface ChorobaRepository extends JpaRepository<Choroba, String> {

    @Query("SELECT c FROM Choroba c WHERE " +
            "LOWER(c.kodIcd10) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.nazwaChoroby) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.opisKliniczny) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Choroba> search(String keyword, Sort sort);
}