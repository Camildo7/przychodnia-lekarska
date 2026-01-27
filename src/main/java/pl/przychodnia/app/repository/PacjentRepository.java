package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Pacjent;
import java.util.List;

public interface PacjentRepository extends JpaRepository<Pacjent, String> {

    List<Pacjent> findByNazwiskoContainingIgnoreCaseOrPeselContaining(String nazwisko, String pesel);
}