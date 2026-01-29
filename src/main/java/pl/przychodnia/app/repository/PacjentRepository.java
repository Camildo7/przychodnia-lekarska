package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Pacjent;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacjentRepository extends JpaRepository<Pacjent, String> {

    Page<Pacjent> findByNazwiskoContainingIgnoreCaseOrPeselContaining(String nazwisko, String pesel, Pageable pageable);
}